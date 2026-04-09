package br.com.metaro.portal.modules.general.memorando.services;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.services.NotificationService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoIgnoreDto;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoListDto;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoDto;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoInsertDto;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import br.com.metaro.portal.modules.general.memorando.utils.MemorandoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class MemorandoService {
    @Autowired
    private MemorandoUtil util;
    @Autowired
    private MemorandoRepository memorandoRepository;
    @Autowired
    private MemorandoLogService logService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PositionRepository positionRepository;

    @Transactional(readOnly = true)
    public List<MemorandoListDto> findAll() {
        Sort sort = Sort.by("id").descending();
        List<Memorando> entities = memorandoRepository.findAll(sort);
        entities = util.filterByAccess(entities);
        return entities.stream().map(MemorandoListDto::new).toList();
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

        if (
            dto.getDepartments().size() < 2 &&
            dto.getDepartments().getFirst().equals(me.getPosition().getId())
        ) {
            throw new UnprocessableEntityException("É necessário ao menos 2 departamentos para continuar!");
        }

        Memorando entity = new Memorando();

        util.dtoToEntity(dto, entity);
        util.checkIfAllDepartmentsAreActive(entity);

        entity.setCreatedBy(me);
        entity.setSignatures(new ArrayList<>());
        entity.setLogs(new ArrayList<>());

        util.addMyDepartment(entity);
        util.addNumberAndCreatedAt(entity);

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) util.addAllSignatures(entity);

        entity = memorandoRepository.save(entity);
        logService.create(entity.getId(), "Criou o documento");

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            util.publishPipeline(entity);
            util.checkIfEveryoneHasSigned(entity);
            entity = memorandoRepository.save(entity);
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

        if (
            dto.getDepartments().size() < 2 &&
            dto.getDepartments().getFirst().equals(me.getPosition().getId())
        ) {
            throw new UnprocessableEntityException("É necessário ao menos 2 departamentos para continuar!");
        }

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            util.checkChanges(dto, entity);
        }

        util.dtoToEntity(dto, entity);
        util.addMyDepartment(entity);
        util.checkIfAllDepartmentsAreActive(entity);
        util.addNumberAndCreatedAt(entity);

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            util.addAllSignatures(entity);
            util.publishPipeline(entity);
            util.checkIfEveryoneHasSigned(entity);
        }

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto sign(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Só é possível assinar um Memorando publicado!");
        }

        User me = userService.authenticate();

        util.sign(entity);
        util.removeUserNotification(entity.getId(), me.getId());

        util.checkIfEveryoneHasSigned(entity);
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
        util.removeNotifications(entity);

        logService.create(entity.getId(), "Cancelou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto rollback(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (
            !entity.getStatus().equals(MemorandoStatus.PUBLISH)
            && !entity.getStatus().equals(MemorandoStatus.APPROVED)
        ) {
            throw new UnprocessableEntityException("Somente registros com status publicado ou aprovado podem ser editados!");
        }

        User me = userService.authenticate();

        if (me.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnprocessableEntityException("Somente administradores podem usar a opção rollback");
        }

        entity.setStatus(MemorandoStatus.CREATED);
        logService.create(entity.getId(), "Voltou o documento para status inicial");
        util.removeNotifications(entity);
        util.addAllSignatures(entity);

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto updateSignatures(Long id, MemorandoIgnoreDto dto) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Somente registros com status publicado podem ser atualizados!");
        }

        User me = userService.authenticate();
        if (me.getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnprocessableEntityException("Somente administradores podem atualizar documentos!");
        }

        util.removeUserNotification(entity.getId(), dto.getUserId());

        /// atualizar assinaturas e notificações
        Position department = positionRepository.findById(dto.getDepartmentId()).orElseThrow(ResourceNotFoundException::new);
        entity.getSignatures().removeIf(s -> s.getDepartmentSigned().getId().equals(department.getId()));
        memorandoRepository.flush();

        for (User mananger : department.getManangers()) {
            entity.getSignatures().addLast(new Signature(entity, false, mananger, department));

            notificationService.create("Memorando nº %d - %s".formatted(entity.getNumber(), entity.getTitle()),
                    "/general/memorando/%d".formatted(entity.getId()), false, NotificationType.MEMORANDO,
                    entity.getId(), entity.getCreatedBy(), mananger);
        }

        /// log de atualização
        logService.create(entity.getId(), "Removeu e atualizou as assinaturas de %s".formatted(department.getName()));

        util.checkIfEveryoneHasSigned(entity);
        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Memorando entity = memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.CREATED)) {
            throw new UnprocessableEntityException("Apenas registros com o status CREATED podem ser deletados!");
        }

        if (entity.getNumber() != null) {
            throw new UnprocessableEntityException("Apenas documentos sem número registrado porem ser deletados!");
        }

        memorandoRepository.deleteById(id);
    }
}
