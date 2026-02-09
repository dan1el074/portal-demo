package br.com.metaro.portal.modules.general.internalCommunication.services;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunication;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationStatus;
import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationDto;
import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationInsertDto;
import br.com.metaro.portal.modules.general.internalCommunication.repository.InternalCommunicationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InternalCommunicationService {

    @Autowired
    private InternalCommunicationRepository internalCommunicationRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private InternalCommunicationLogService logService;

    // TODO: no formulário do frontend, poder selecionar mais de um item ao mesmo tempo
    // TODO: apenas informar o número se o status for diferente de "CREATED"
    // TODO: posso editar as CIs que tiverem o status "CREATED",
    // TODO: no frontend, trocar o botão de edição por um ícone de olho no painel "Publicados", ou mostrar popup
    // TODO: se o status for "PUBLISH", só posso editar se for administrador!
    //       ...depois de editar, as assinaturas precisam ser coletadas novamente
    // TODO: posso apagar CIS que não tiverem números
    // TODO: as CIs só podem ser canceladas se estiverem com status "PUBLISH"
    // TODO: só posso assinar CIs com o status "PUBLISH"

    @Transactional(readOnly = true)
    public List<InternalCommunicationDto> findAll() {
        List<InternalCommunication> entities = internalCommunicationRepository.findAll();
        entities = filterByAccess(entities);
        return entities.stream().map(InternalCommunicationDto::new).toList();
    }

    @Transactional(readOnly = true)
    public InternalCommunicationDto findById(Long id) {
        InternalCommunication entity = internalCommunicationRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return new InternalCommunicationDto(validByAccess(entity));
    }

    @Transactional
    public InternalCommunicationDto insert(InternalCommunicationInsertDto dto) {
        User me = userService.authenticate();
        InternalCommunication entity = new InternalCommunication();

        applyInsertRules(dto, entity);
        entity.setCreatedBy(me);
//        entity.setInteractions(new ArrayList<>());
//        entity.getInteractions().add(me);
        entity.setLogs(new ArrayList<>());

        entity = internalCommunicationRepository.save(entity);
        logService.create(entity.getId(), "Criou o documento");
        if (dto.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public InternalCommunicationDto update(Long id, InternalCommunicationInsertDto dto) {
        InternalCommunication entity = internalCommunicationRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Não é possível editar CIs finalizadas!");
        }

        User me = userService.authenticate();
        if (
            me.getRoles().stream().noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))
            && !me.getId().equals(entity.getCreatedBy().getId())
        ) {
            throw new ForbiddenException("Você só pode editar as CIs que criou!");
        }

        applyInsertRules(dto, entity);

//        if (entity.getInteractions().stream().noneMatch(currentUser -> currentUser.getId().equals(me.getId()))) {
//            entity.getInteractions().add(me);
//        }

        entity = internalCommunicationRepository.save(entity);
        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public InternalCommunicationDto disable(Long id) {
        InternalCommunication entity =  internalCommunicationRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Não é possível cancelar CIs finalizadas!");
        }

        User me = userService.authenticate();
        if (
            me.getAuthorities().stream().noneMatch(role ->
                    role.getAuthority().equals("ROLE_ADMIN"))
            && !me.getId().equals(entity.getCreatedBy().getId())
        ) {
            throw new ForbiddenException("Você só pode cancelar as CIs que criou!");
        }

        entity.setStatus(InternalCommunicationStatus.CANCELED);
        internalCommunicationRepository.save(entity);
        return new InternalCommunicationDto(entity);
    }

    private List<InternalCommunication> filterByAccess(List<InternalCommunication> entities) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entities;
        }

        entities = entities.stream().filter(entity ->
            (!entity.getStatus().equals(InternalCommunicationStatus.CREATED)
            && !entity.getStatus().equals(InternalCommunicationStatus.CANCELED))
            || entity.getCreatedBy().getId().equals(me.getId())
        ).toList();

        return entities;
    }

    private InternalCommunication validByAccess(InternalCommunication entity) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entity;
        }

        if (
            (entity.getStatus().equals(InternalCommunicationStatus.CREATED)
            || entity.getStatus().equals(InternalCommunicationStatus.CANCELED))
            && !entity.getCreatedBy().getId().equals(me.getId())
        )  throw new ForbiddenException("Você não tem permissões para acessar esse recurso!");

        return entity;
    }

    private void applyInsertRules(@NotNull InternalCommunicationInsertDto dto, @NotNull InternalCommunication entity) {
        entity.setNumber(paramService.newInternalControl());
        entity.setCreateAt(Instant.now());
        entity.setRequest(dto.getRequest());
        entity.setClient(dto.getClient());
        entity.setItem(dto.getItem());
        entity.setDescription(dto.getDescription());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        entity.setFromDepartments(new ArrayList<>());

        List<Long> positionList = Arrays.stream(dto.getDepartments().split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();

        for (Long positionId : positionList) {
            Position position = positionRepository.getReferenceById(positionId);
            entity.getFromDepartments().add(position);
        }
    }

}
