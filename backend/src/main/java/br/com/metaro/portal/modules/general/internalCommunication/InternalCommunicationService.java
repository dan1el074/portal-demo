package br.com.metaro.portal.modules.general.internalControl;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.internalControl.dots.InternalControlDto;
import br.com.metaro.portal.modules.general.internalControl.dots.InternalControlInsertDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class InternalControlService {
    @Autowired
    private InternalControlRepository internalControlRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ParamService paramService;

    @Transactional(readOnly = true)
    public List<InternalControlDto> findAll() {
        List<InternalControl> entities = internalControlRepository.findAll();
        entities = filterByAccess(entities);
        return entities.stream().map(InternalControlDto::new).toList();
    }

    @Transactional(readOnly = true)
    public InternalControlDto findById(Long id) {
        InternalControl entity = internalControlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
        entity = validByAccess(entity);
        return new InternalControlDto(entity);
    }

    @Transactional
    public InternalControlDto insert(InternalControlInsertDto dto) {
        User me = userService.authenticate();
        InternalControl entity = new InternalControl();

        applyInsertRules(dto, entity);
        entity.setUser(me);
        entity.setInteractions(new ArrayList<>());
        entity.getInteractions().add(me);

        entity = internalControlRepository.save(entity);
        return new InternalControlDto(entity);
    }

    @Transactional
    public InternalControlDto update(Long id, InternalControlInsertDto dto) {
        InternalControl entity = internalControlRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getStatus().equals(InternalControlStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Não é possível editar CIs finalizadas!");
        }

        User me = userService.authenticate();
        if (
            !me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))
            && !me.getId().equals(entity.getUser().getId())
        ) {
            throw new ForbiddenException("Você só pode editar as CIs que criou!");
        }

        applyInsertRules(dto, entity);

        if (!entity.getInteractions().stream().anyMatch(currentUser -> currentUser.getId().equals(me.getId()))) {
            entity.getInteractions().add(me);
        }
        entity = internalControlRepository.save(entity);
        return new InternalControlDto(entity);
    }

    @Transactional
    public InternalControlDto disable(Long id) {
        InternalControl entity =  internalControlRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getStatus().equals(InternalControlStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Não é possível cancelar CIs finalizadas!");
        }

        User me = userService.authenticate();
        if (
            !me.getAuthorities().stream().anyMatch(role -> {
                return role.getAuthority().equals("ROLE_ADMIN");
            })
            && !me.getId().equals(entity.getUser().getId())
        ) {
            throw new ForbiddenException("Você só pode cancelar as CIs que criou!");
        }

        entity.setStatus(InternalControlStatus.CANCELED);
        internalControlRepository.save(entity);
        return new InternalControlDto(entity);
    }

    private List<InternalControl> filterByAccess(List<InternalControl> entities) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entities;
        }

        entities = entities.stream().filter(entity -> {
            if (
                    (entity.getStatus().equals(InternalControlStatus.CREATED) ||
                            entity.getStatus().equals(InternalControlStatus.CANCELED)) &&
                            !entity.getUser().getId().equals(me.getId())
            )  return false;
            return true;
        }).toList();

        return entities;
    }

    private InternalControl validByAccess(InternalControl entity) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entity;
        }

        if (
            (entity.getStatus().equals(InternalControlStatus.CREATED)
            || entity.getStatus().equals(InternalControlStatus.CANCELED))
            && !entity.getUser().getId().equals(me.getId())
        )  throw new ForbiddenException("Você não tem permissões para acessar esse recurso!");

        return entity;
    }

    private void applyInsertRules(@NotNull InternalControlInsertDto dto, @NotNull InternalControl entity) {
        entity.setNumber(paramService.newInternalControl());
        entity.setCreateAt(Instant.now());

        entity.setRequest(dto.getRequest());
        entity.setClient(dto.getClient());           // TODO: com base no número do pedido (request), buscar no Probus
        entity.setItem(dto.getItem());               // TODO: pegar esse cara no Probus também, usando "request"
        entity.setDescription(dto.getDescription());
        entity.setReason(dto.getReason());
        entity.setFromDepartments(new ArrayList<>());

        for (PositionDto positionDto : dto.getDepartments()) {
            Position position = positionRepository.getReferenceById(positionDto.getId());
            entity.getFromDepartments().add(position);
        }

        if (dto.getStatus().equals(null)) {
            entity.setStatus(InternalControlStatus.CREATED);
        } else {
            entity.setStatus(dto.getStatus());
        }
    }
}
