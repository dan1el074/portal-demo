package br.com.metaro.portal.modules.general.memorando.services;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.NotificationService;
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
import br.com.metaro.portal.modules.general.memorando.utils.MemorandoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemorandoService {

    @Autowired
    private MemorandoRepository memorandoRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MemorandoLogService logService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MemorandoUtil util;

    @Transactional(readOnly = true)
    public List<MemorandoDto> findAll() {
        Sort sort = Sort.by("id").descending();
        List<Memorando> entities = memorandoRepository.findAll(sort);
        entities = util.filterByAccess(entities);
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

        util.dtoToEntity(dto, entity);
        util.checkIfAllDepartmentsAreActive(entity);

        entity.setCreatedBy(me);
        entity.setSignatures(new ArrayList<>());
        entity.setLogs(new ArrayList<>());

        util.addMyDepartment(entity);
        util.addNumberAndCreatedAt(entity);

        entity = memorandoRepository.save(entity);
        logService.create(entity.getId(), "Criou o documento");

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            util.publishPipeline(entity);
            util.checkIfEveryoneHasSigned(entity);
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
            util.checkChanges(dto, entity);
        }

        util.dtoToEntity(dto, entity);
        util.addMyDepartment(entity);
        util.checkIfAllDepartmentsAreActive(entity);
        util.addNumberAndCreatedAt(entity);

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            util.publishPipeline(entity);
        }

        util.checkIfEveryoneHasSigned(entity);
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

        /// verifica se eu já assinei
        if (entity.getSignatures().stream().anyMatch(x -> x.getUser().getId().equals(me.getId()))) {
            throw new UnprocessableEntityException("Você já assinou esse documento!");
        }

        /// assina somente se eu for um dos gestores
        boolean signed = false;

        for (Position department : entity.getFromDepartments()) {
            for (User mananger : department.getManangers()) {
                if (!mananger.getId().equals(me.getId())) continue;

                entity.getSignatures().add(new Signature(entity, me, department));
                logService.create(entity.getId(), "Assinou o documento (%s)".formatted(department.getName()));
                signed = true;
            }
        }

        if (!signed) throw new UnprocessableEntityException("Somente gestores podem assinar o Memorando!");

        /// remove minha notificação
        for (NotificationDto notification : notificationService.listByUser(me.getId())) {
            if (
                notification.getType().equals(NotificationType.MEMORANDO.name())
                && notification.getReferenceId().equals(entity.getId())
            ) {
                notificationService.delete(notification.getId(), me.getId());
            }
        }

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
            throw new UnprocessableEntityException("Somente administradores podem usar a opção \"rollback\"");
        }

        entity.setStatus(MemorandoStatus.CREATED);
        logService.create(entity.getId(), "Voltou o documento para status inicial");
        util.removeNotifications(entity);

        /// remove todas as assinaturas
        entity.getSignatures().clear();
        memorandoRepository.flush();

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
}
