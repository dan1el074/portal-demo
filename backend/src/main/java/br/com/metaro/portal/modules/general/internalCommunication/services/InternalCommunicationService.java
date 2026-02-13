package br.com.metaro.portal.modules.general.internalCommunication.services;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.internalCommunication.entities.Interaction;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunication;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationStatus;
import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationDto;
import br.com.metaro.portal.modules.general.internalCommunication.dots.InternalCommunicationInsertDto;
import br.com.metaro.portal.modules.general.internalCommunication.repository.InternalCommunicationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

    @Transactional(readOnly = true)
    public List<InternalCommunicationDto> findAll() {
        Sort sort = Sort.by("id").descending();
        List<InternalCommunication> entities = internalCommunicationRepository.findAll(sort);
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

        dtoToEntity(dto, entity);
        entity.setCreatedBy(me);
        entity.setInteractions(new HashSet<>());
        entity.setLogs(new ArrayList<>());

        if (entity.getFromDepartments().stream().noneMatch(x -> x.getId().equals(me.getPosition().getId()))) {
            entity.getFromDepartments().addFirst(me.getPosition());
        }

        if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            entity.setCreateAt(Instant.now());
            entity.setNumber(paramService.newInternalControl());
            entity.getInteractions().add(new Interaction(entity, me, me.getPosition()));
        }

        entity = internalCommunicationRepository.save(entity);

        logService.create(entity.getId(), "Criou o documento");
        if (dto.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
            logService.create(entity.getId(), "Assinou o documento");
        }

        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public InternalCommunicationDto update(Long id, InternalCommunicationInsertDto dto) {
        InternalCommunication entity = internalCommunicationRepository
                .findById(id).orElseThrow(ResourceNotFoundException::new);

        boolean wasCreated = entity.getStatus().equals(InternalCommunicationStatus.CREATED);

        if (
            entity.getStatus().equals(InternalCommunicationStatus.APPROVED)
            || entity.getStatus().equals(InternalCommunicationStatus.CANCELED)
        ) {
            throw new UnprocessableEntityException("Não é possível editar CIs finalizadas ou canceladas!");
        }

        User me = userService.authenticate();
        if (me.getRoles().stream().noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
                throw new ForbiddenException("Somente administradores podem editar CIs publicadas!");
            }
            if (!me.getId().equals(entity.getCreatedBy().getId())) {
                throw new ForbiddenException("Você só pode editar as CIs que criou!");
            }
        }

        if (dto.getDepartments().split(",").length < 2) {
            throw new UnprocessableEntityException("É necessário ao menos 2 departamentos para assinaturas");
        }

        if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            checkChanges(dto, entity);
        }

        dtoToEntity(dto, entity);

        /// verificar se o status mudou de CREATED para PUBLISH
        if (entity.getStatus().equals(InternalCommunicationStatus.PUBLISH) && wasCreated) {
            entity.setCreateAt(Instant.now());
            entity.setNumber(paramService.newInternalControl());
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        /// verificar se o departamento de origem está listado (se não, adicioná-lo no início)
        InternalCommunication finalNewEntity = entity;
        if (entity.getFromDepartments().stream().noneMatch(x ->
                x.getId().equals(finalNewEntity.getCreatedBy().getPosition().getId()))) {
            entity.getFromDepartments().addFirst(finalNewEntity.getCreatedBy().getPosition());
        }

        /// remover todas as assinaturas
        entity.getInteractions().clear();
        internalCommunicationRepository.flush();

        /// se for o dono do post, assina como no insert()
        if (
            entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)
            && me.getId().equals(entity.getCreatedBy().getId())
        ) {
            entity.getInteractions().add(new Interaction(entity, me, me.getPosition()));
            logService.create(entity.getId(), "Assinou o documento");
        }

        /// administrador não assina, a menos que seu departamento esteja listado!!
        if (
            !me.getId().equals(entity.getCreatedBy().getId())
            && entity.getFromDepartments().stream().anyMatch(x -> x.getId().equals(me.getPosition().getId()))
        ) {
            entity.getInteractions().add(new Interaction(entity, me, me.getPosition()));
            logService.create(entity.getId(), "Assinou o documento");
        }

        entity = internalCommunicationRepository.save(entity);
        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public InternalCommunicationDto disable(Long id) {
        InternalCommunication entity =  internalCommunicationRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Só é possível cancelar CIs publicadas!");
        }

        User me = userService.authenticate();
        if (me.getAuthorities().stream().noneMatch(role ->
                    role.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("Apenas administradores podem cancelar CIs!");
        }

        entity.setStatus(InternalCommunicationStatus.CANCELED);
        internalCommunicationRepository.save(entity);
        logService.create(entity.getId(), "Cancelou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public InternalCommunicationDto sign(Long id) {
        InternalCommunication entity = internalCommunicationRepository
                .findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(InternalCommunicationStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Só é possível assinar CIs publicadas!");
        }

        User me = userService.authenticate();

        if (entity.getFromDepartments().stream().noneMatch(department ->
                department.getId().equals(me.getPosition().getId()))) {
            throw new UnprocessableEntityException("Seu departamento não está listado!");
        }
        if (entity.getInteractions().stream().anyMatch(interaction ->
                interaction.getDepartmentSigned().getId().equals(me.getPosition().getId()))) {
            throw new UnprocessableEntityException("Seu departamento já assinou essa CI!");
        }

        entity.getInteractions().add(new Interaction(entity, me, me.getPosition()));
        logService.create(entity.getId(), "Assinou o documento");

        if (entity.getFromDepartments().size() == entity.getInteractions().size()) {
            entity.setStatus(InternalCommunicationStatus.APPROVED);
            logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                    .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        entity = internalCommunicationRepository.save(entity);
        return new InternalCommunicationDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!internalCommunicationRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        internalCommunicationRepository.deleteById(id);
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

    private void checkChanges(InternalCommunicationInsertDto dto, @NotNull InternalCommunication entity) {
        if (!dto.getItem().equals(entity.getItem())) {
            logService.create(entity.getId(), "Alterou o item de \"%s\" para \"%s\""
                    .formatted(entity.getItem(), dto.getItem()));
        }
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
    }

    private void diffPositions(@NotNull InternalCommunicationInsertDto dto, @NotNull InternalCommunication entity) {
        List<Position> dtoDepartments = new ArrayList<>();
        List<Long> dtoDepartmentsId = Arrays.stream(dto.getDepartments().split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();

        for (Long departmentId : dtoDepartmentsId) {
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

    private void dtoToEntity(@NotNull InternalCommunicationInsertDto dto, @NotNull InternalCommunication entity) {
        entity.setRequest(dto.getRequest());
        entity.setClient(dto.getClient());
        entity.setItem(dto.getItem());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        entity.setFromDepartments(new ArrayList<>());

        List<Long> positionList = Arrays.stream(dto.getDepartments().split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();

        for (Long positionId : positionList) {
            Position position = positionRepository.findById(positionId).orElseThrow(ResourceNotFoundException::new);
            entity.getFromDepartments().add(position);
        }
    }
}
