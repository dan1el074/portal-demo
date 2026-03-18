package br.com.metaro.portal.modules.general.memorando.services;

import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.memorando.entities.Interaction;
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
import java.util.Arrays;
import java.util.HashSet;
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

    @Transactional(readOnly = true)
    public List<MemorandoDto> findAll() {
        Sort sort = Sort.by("id").descending();
        List<Memorando> entities = memorandoRepository.findAll(sort);
        entities = filterByAccess(entities);
        return entities.stream().map(MemorandoDto::new).toList();
    }

    @Transactional(readOnly = true)
    public MemorandoDto findById(Long id) {
        Memorando entity = memorandoRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return new MemorandoDto(validByAccess(entity));
    }

    @Transactional
    public MemorandoDto insert(MemorandoInsertDto dto) {
        User me = userService.authenticate();
        Memorando entity = new Memorando();

        dtoToEntity(dto, entity);
        entity.setCreatedBy(me);
        entity.setInteractions(new HashSet<>());
        entity.setLogs(new ArrayList<>());

        if (entity.getFromDepartments().stream().noneMatch(x -> x.getId().equals(me.getPosition().getId()))) {
            entity.getFromDepartments().addFirst(me.getPosition());
        }

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            entity.setCreateAt(Instant.now());
            entity.setNumber(paramService.newInternalControl());
            entity.getInteractions().add(new Interaction(entity, me, me.getPosition()));
        }

        entity = memorandoRepository.save(entity);

        logService.create(entity.getId(), "Criou o documento");
        if (dto.getStatus().equals(MemorandoStatus.PUBLISH)) {
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
            logService.create(entity.getId(), "Assinou o documento");
        }

        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto update(Long id, MemorandoInsertDto dto) {
        Memorando entity = memorandoRepository
                .findById(id).orElseThrow(ResourceNotFoundException::new);

        boolean wasCreated = entity.getStatus().equals(MemorandoStatus.CREATED);

        if (
            entity.getStatus().equals(MemorandoStatus.APPROVED)
            || entity.getStatus().equals(MemorandoStatus.CANCELED)
        ) {
            throw new UnprocessableEntityException("Não é possível editar CIs finalizadas ou canceladas!");
        }

        User me = userService.authenticate();
        if (me.getRoles().stream().noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
                throw new ForbiddenException("Somente administradores podem editar CIs publicadas!");
            }
            if (!me.getId().equals(entity.getCreatedBy().getId())) {
                throw new ForbiddenException("Você só pode editar as CIs que criou!");
            }
        }

        if (dto.getDepartments().split(",").length < 2) {
            throw new UnprocessableEntityException("É necessário ao menos 2 departamentos para assinaturas");
        }

        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            checkChanges(dto, entity);
        }

        dtoToEntity(dto, entity);

        /// verificar se o status mudou de CREATED para PUBLISH
        if (entity.getStatus().equals(MemorandoStatus.PUBLISH) && wasCreated) {
            entity.setCreateAt(Instant.now());
            entity.setNumber(paramService.newInternalControl());
            logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                    entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        /// verificar se o departamento de origem está listado (se não, adicioná-lo no início)
        Memorando finalNewEntity = entity;
        if (entity.getFromDepartments().stream().noneMatch(x ->
                x.getId().equals(finalNewEntity.getCreatedBy().getPosition().getId()))) {
            entity.getFromDepartments().addFirst(finalNewEntity.getCreatedBy().getPosition());
        }

        /// remover todas as assinaturas
        entity.getInteractions().clear();
        memorandoRepository.flush();

        /// se for o dono do post, assina como no insert()
        if (
            entity.getStatus().equals(MemorandoStatus.PUBLISH)
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

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto disable(Long id) {
        Memorando entity =  memorandoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            throw new UnprocessableEntityException("Só é possível cancelar CIs publicadas!");
        }

        User me = userService.authenticate();
        if (me.getAuthorities().stream().noneMatch(role ->
                    role.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("Apenas administradores podem cancelar CIs!");
        }

        entity.setStatus(MemorandoStatus.CANCELED);
        memorandoRepository.save(entity);
        logService.create(entity.getId(), "Cancelou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        return new MemorandoDto(entity);
    }

    @Transactional
    public MemorandoDto sign(Long id) {
        Memorando entity = memorandoRepository
                .findById(id).orElseThrow(ResourceNotFoundException::new);

        if (!entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
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
            entity.setStatus(MemorandoStatus.APPROVED);
            logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                    .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));
        }

        entity = memorandoRepository.save(entity);
        return new MemorandoDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!memorandoRepository.existsById(id)) {
            throw new ResourceNotFoundException();
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

    private Memorando validByAccess(Memorando entity) {
        User me = userService.authenticate();
        if (me.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            return entity;
        }

        if (
            (entity.getStatus().equals(MemorandoStatus.CREATED)
            || entity.getStatus().equals(MemorandoStatus.CANCELED))
            && !entity.getCreatedBy().getId().equals(me.getId())
        )  throw new ForbiddenException("Você não tem permissões para acessar esse recurso!");

        return entity;
    }

    private void checkChanges(MemorandoInsertDto dto, @NotNull Memorando entity) {
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

    private void diffPositions(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
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

    private void dtoToEntity(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
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
