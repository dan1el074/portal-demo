package br.com.metaro.portal.modules.general.memorando.services;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.NotificationService;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoDto;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoInsertDto;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemorandoService {

    @Autowired
    private MemorandoRepository memorandoRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private MemorandoLogService logService;
    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<MemorandoDto> findAll() {
        Sort sort = Sort.by("id").descending();
        List<Memorando> entities = memorandoRepository.findAll(sort);
        entities = filterByAccess(entities);
        return entities.stream().map(MemorandoDto::new).toList();
    }

    @Transactional(readOnly = true)
    public MemorandoDto findById(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        User me = userService.authenticate();

        if (
            entity.getStatus().equals(MemorandoStatus.CREATED)
            && !entity.getCreatedBy().getId().equals(me.getId())
            && me.getRoles().stream().noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))
        )  throw new ForbiddenException("Você não tem permissões para acessar esse recurso!");

        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto insert(MemorandoInsertDto dto) {
        User me = userService.authenticate();
        Memorando entity = new Memorando();

        dtoToEntity(dto, entity);
        entity.setCreatedBy(me);
        entity.setSignatures(new ArrayList<>());
        entity.setLogs(new ArrayList<>());

        if (entity.getFromDepartments().stream().noneMatch(x -> x.getId().equals(me.getPosition().getId()))) {
            entity.getFromDepartments().addFirst(me.getPosition());
        }

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            entity.setCreateAt(Instant.now());
            entity.setNumber(paramService.newInternalControl());
        }

        entity = memorandoRepository.save(entity);
        logService.create(entity.getId(), "Criou o documento");

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

            for (Position department : entity.getFromDepartments()) {
                for (User mananger : department.getManangers()) {
                    /// assina se for gestor de alguma área
                    if (mananger.getId().equals(me.getId())) {
                        entity.getSignatures().add(new Signature(entity, me, department));
                        logService.create(entity.getId(), "Assinou o documento (%s)".formatted(department.getName()));
                        continue;
                    }

                    /// manda as notificações para os gestores
                    notificationService.create("Memorando nº %d - %s".formatted(entity.getNumber(),entity.getTitle()),
                            "/general/memorando/%d".formatted(entity.getId()),false, NotificationType.MEMORANDO,
                            entity.getId(), me, mananger);
                }
            }
        }

        /// verifica se todos já assinaram
        boolean leftSign = false;

        for (Position departments : entity.getFromDepartments()) {
            for (User mananger : departments.getManangers()) {
                if (entity.getSignaturesUsers().stream().noneMatch(x -> x.getId().equals(mananger.getId()))) {
                    leftSign = true;
                    break;
                }
            }

            if (leftSign) break;
        }

        if (!leftSign) {
            entity.setStatus(MemorandoStatus.APPROVED);
            logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                    .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto update(Long id, MemorandoInsertDto dto) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.CREATED)) {
            throw new UnprocessableEntityException("Só é possível editar um Memorando com o status \"Salvo\"!");
        }

        User me = userService.authenticate();
        if (!me.getId().equals(entity.getCreatedBy().getId())) {
            throw new ForbiddenException("Você só pode editar um Memorando que criou!");
        }

        if (dto.getDepartments().size() < 2) {
            throw new UnprocessableEntityException("É necessário ao menos 2 departamentos para continuar!");
        }

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            checkChanges(dto, entity);
        }

        dtoToEntity(dto, entity);

        /// verificar se o departamento de origem está listado (se não, adiciona no início)
        Memorando finalmemorando = entity;
        if (entity.getFromDepartments().stream().noneMatch(x ->
                x.getId().equals(finalmemorando.getCreatedBy().getPosition().getId()))) {
            entity.getFromDepartments().addFirst(finalmemorando.getCreatedBy().getPosition());
        }

        /// verificar se o status mudou para PUBLISH
        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            if (entity.getNumber() == null) entity.setNumber(paramService.newInternalControl());
            if (entity.getCreateAt() == null) entity.setCreateAt(Instant.now());

            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

            for (Position department : entity.getFromDepartments()) {
                for (User mananger : department.getManangers()) {
                    /// assina se for gestor de alguma área
                    if (mananger.getId().equals(me.getId())) {
                        entity.getSignatures().add(new Signature(entity, me, department));
                        logService.create(entity.getId(), "Assinou o documento (%s)".formatted(department.getName()));
                        continue;
                    }

                    /// manda as notificações para os gestores
                    notificationService.create("Memorando nº %d - %s".formatted(entity.getNumber(),entity.getTitle()),
                            "/general/memorando/%d".formatted(entity.getId()),false, NotificationType.MEMORANDO,
                            entity.getId(), me, mananger);
                }
            }
        }

        /// verifica se todos já assinaram
        boolean leftSign = false;

        for (Position departments : entity.getFromDepartments()) {
            for (User mananger : departments.getManangers()) {
                if (entity.getSignaturesUsers().stream().noneMatch(x -> x.getId().equals(mananger.getId()))) {
                    leftSign = true;
                    break;
                }
            }

            if (leftSign) break;
        }

        if (!leftSign) {
            entity.setStatus(MemorandoStatus.APPROVED);
            logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                    .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto disable(Long id) {
        Memorando entity =  memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getStatus().equals(MemorandoStatus.CREATED)) {
            throw new UnprocessableEntityException("Não é possível cancelar um Memorando com status \"Salvo\"!");
        }

        User me = userService.authenticate();
        if (me.getAuthorities().stream().noneMatch(role ->
                    role.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("Apenas administradores podem cancelar um Memorando!");
        }

        entity.setStatus(MemorandoStatus.CANCELED);
        memorandoRepository.save(entity);

        /// remove todas as notificações
        List<Notification> notifications = notificationService.findByReferenceIdAndType(entity.getId(), NotificationType.MEMORANDO);
        for (Notification notification : notifications) {
            notificationService.delete(notification.getId(), notification.getUser().getId());
        }

        /// remove todos os logs
        logService.create(entity.getId(), "Cancelou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto sign(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Só é possível assinar um Memorando publicado!");
        }

        User me = userService.authenticate();
        boolean signed = false;

        /// verifica se eu já assinei
        if (entity.getSignatures().stream().anyMatch(x -> x.getUser().getId().equals(me.getId()))) {
            throw new UnprocessableEntityException("Você já assinou esse documento!");
        }

        /// assina somente se eu for um dos gestores
        for (Position department : entity.getFromDepartments()) {
            for (User mananger : department.getManangers()) {
                if (mananger.getId().equals(me.getId())) {
                    entity.getSignatures().add(new Signature(entity, me, department));
                    logService.create(entity.getId(), "Assinou o documento (%s)".formatted(department.getName()));
                    signed = true;
                }
            }
        }

        if (!signed) throw new UnprocessableEntityException("Somente gestores podem assinar o Memorando!");

        /// remove a notificação do usuário
        for (NotificationDto notification : notificationService.listByUser(me.getId())) {
            if (
                notification.getType().equals(NotificationType.MEMORANDO.name())
                && notification.getReferenceId().equals(entity.getId())
            ) {
                notificationService.delete(notification.getId(), me.getId());
            }
        }

        /// verifica se todos já assinaram
        boolean leftSign = false;

        for (Position departments : entity.getFromDepartments()) {
            for (User mananger : departments.getManangers()) {
                if (entity.getSignaturesUsers().stream().noneMatch(x -> x.getId().equals(mananger.getId()))) {
                    leftSign = true;
                    break;
                }
            }

            if (leftSign) break;
        }

        if (!leftSign) {
             entity.setStatus(MemorandoStatus.APPROVED);
             logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                     .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

            /// remove todas as notificações
            List<Notification> notifications = notificationService.findByReferenceIdAndType(entity.getId(), NotificationType.MEMORANDO);
            for (Notification notification : notifications) {
                notificationService.delete(notification.getId(), notification.getUser().getId());
            }
        }

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto rollback(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (
            !entity.getStatus().equals(MemorandoStatus.PUBLISH)
            && !entity.getStatus().equals(MemorandoStatus.APPROVED)
        ) {
            throw new UnprocessableEntityException("Só é possível editar registros com status publicado ou aprovado!");
        }

        User me = userService.authenticate();

        if (me.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnprocessableEntityException("Somente administradores podem usar a opção \"rollback\"");
        }

        /// altera o status para CREATED
        entity.setStatus(MemorandoStatus.CREATED);
        logService.create(entity.getId(), "Voltou o documento para status inicial");

        /// remove todas as assinaturas
        entity.getSignatures().clear();
        memorandoRepository.flush();

        /// remove todas as notificações
        List<Notification> notifications = notificationService.findByReferenceIdAndType(entity.getId(), NotificationType.MEMORANDO);
        for (Notification notification : notifications) {
            notificationService.delete(notification.getId(), notification.getUser().getId());
        }

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.CREATED)) {
            throw new UnprocessableEntityException("Apenas registros com o status \"Salvo\" podem ser deletados!");
        }

        if (entity.getNumber() != null) {
            throw new UnprocessableEntityException("Apenas registros sem número registrado porem ser deletados!");
        }

        memorandoRepository.deleteById(id);
    }

    private List<Memorando> filterByAccess(List<Memorando> entities) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entities;
        }

        entities = entities.stream().filter(entity ->
            (!entity.getStatus().equals(MemorandoStatus.CREATED)
            && !entity.getStatus().equals(MemorandoStatus.CANCELED))
            || entity.getCreatedBy().getId().equals(me.getId())
        ).toList();

        return entities;
    }

    private void dtoToEntity(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
        entity.setRequest(dto.getRequest());
        entity.setClient(dto.getClient());
        entity.setItems(new ArrayList<>());
        entity.getItems().addAll(dto.getItems());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        entity.setFromDepartments(new ArrayList<>());

        for (Long positionId : dto.getDepartments()) {
            Position position = positionRepository.findById(positionId).orElseThrow(ResourceNotFoundException::new);
            entity.getFromDepartments().add(position);
        }
    }

    private void checkChanges(MemorandoInsertDto dto, @NotNull Memorando entity) {
        if (!dto.getTitle().equals(entity.getTitle())) {
            logService.create(entity.getId(), "Alterou o título de \"%s\" para \"%s\""
                    .formatted(entity.getTitle(), dto.getTitle()));
        }
        if (!dto.getDescription().equals(entity.getDescription())) {
            logService.create(entity.getId(), "Alterou a descrição de \"%s\" para \"%s\""
                    .formatted(entity.getDescription().replaceAll("<br>", " "),
                            dto.getDescription().replaceAll("<br>", " ")));
        }
        if (!dto.getReason().equals(entity.getReason())) {
            logService.create(entity.getId(), "Alterou o motivo de \"%s\" para \"%s\""
                    .formatted(entity.getReason(), dto.getReason()));
        }

        diffPositions(dto, entity);
        diffItems(dto, entity);
    }

    private void diffPositions(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
        List<Position> dtoDepartments = new ArrayList<>();

        for (Long departmentId : dto.getDepartments()) {
            Position position = positionRepository.findById(departmentId).orElseThrow(ResourceNotFoundException::new);
            dtoDepartments.add(position);
        }

        List<Position> addPositions = dtoDepartments.stream().filter(currentPosition ->
                !entity.getFromDepartments().contains(currentPosition)).toList();

        List<Position> leftPositions = entity.getFromDepartments().stream().filter(currentPosition ->
                !dtoDepartments.contains(currentPosition)).toList();

        if (!leftPositions.isEmpty()) {
            String nameOfDepartments = "";

            for (int i=0; i<leftPositions.size(); i++) {
                if (i > 0) nameOfDepartments = nameOfDepartments.concat(", ");
                if (leftPositions.get(i).getId().equals(entity.getCreatedBy().getPosition().getId())) continue;
                nameOfDepartments = nameOfDepartments.concat(leftPositions.get(i).getName());
            }

            logService.create(entity.getId(), "Removeu o(s) departamento(s) \"%s\"".formatted(nameOfDepartments));
        }

        if (!addPositions.isEmpty()) {
            String nameOfDepartments = "";

            for (int i=0; i<addPositions.size(); i++) {
                if (i > 0) nameOfDepartments = nameOfDepartments.concat(", ");
                nameOfDepartments = nameOfDepartments.concat(addPositions.get(i).getName());
            }

            logService.create(entity.getId(), "Adicionou o(s) departamento(s) \"%s\"".formatted(nameOfDepartments));
        }
    }

    private void diffItems(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
        List<String> addItems = dto.getItems().stream().filter(currentItem ->
                !entity.getItems().contains(currentItem)).toList();

        List<String> leftItems = entity.getItems().stream().filter(currentItem ->
                !dto.getItems().contains(currentItem)).toList();

        if (!addItems.isEmpty()) {
            String itemsChanged = "";

            for (int i=0; i<addItems.size(); i++) {
                if (i > 0) itemsChanged = itemsChanged.concat(", ");
                itemsChanged = itemsChanged.concat(addItems.get(i));
            }

            logService.create(entity.getId(), "Adicionou o(s) item(s) \"%s\"".formatted(itemsChanged));
        }

        if (!leftItems.isEmpty()) {
            String itemsChanged = "";

            for (int i=0; i<leftItems.size(); i++) {
                if (i > 0) itemsChanged = itemsChanged.concat(", ");
                itemsChanged = itemsChanged.concat(leftItems.get(i));
            }

            logService.create(entity.getId(), "Removeu o(s) item(s) \"%s\"".formatted(itemsChanged));
        }
    }
}
