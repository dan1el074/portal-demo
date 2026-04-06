package br.com.metaro.portal.modules.general.memorando.utils;

import br.com.metaro.portal.core.entities.Notification;
import br.com.metaro.portal.core.entities.NotificationType;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.NotificationService;
import br.com.metaro.portal.core.services.ParamService;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.memorando.dots.MemorandoInsertDto;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoStatus;
import br.com.metaro.portal.modules.general.memorando.entities.Signature;
import br.com.metaro.portal.modules.general.memorando.services.MemorandoLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MemorandoUtil {
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MemorandoLogService logService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private PositionRepository positionRepository;

    public void dtoToEntity(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
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

    public List<Memorando> filterByAccess(List<Memorando> entities) {
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

    public void checkIfAllDepartmentsAreActive(@NotNull Memorando entity) {
        /// verifica se algum departamento está desativado
        if (entity.getFromDepartments().stream().anyMatch(p -> p.getActivated().equals(false))) {
            throw new UnprocessableEntityException("Um ou mais departamentos estão desativados!");
        }
    }

    public void checkIfEveryoneHasSigned(@NotNull Memorando entity) {
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
    }

    public void publishPipeline(@NotNull Memorando entity) {
        User me = userService.authenticate();

        logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

        for (Position department : entity.getFromDepartments()) {
            for (User mananger : department.getManangers()) {
                /// assina se eu for gestor de alguma área
                if (mananger.getId().equals(me.getId())) {
                    entity.getSignatures().add(new Signature(entity, me, department));
                    logService.create(entity.getId(), "Assinou o documento (%s)".formatted(department.getName()));
                    continue;
                }

                /// manda as notificações para os gestores
                notificationService.create("Memorando nº %d - %s".formatted(entity.getNumber(), entity.getTitle()),
                        "/general/memorando/%d".formatted(entity.getId()), false, NotificationType.MEMORANDO,
                        entity.getId(), me, mananger);
            }
        }
    }

    public void addNumberAndCreatedAt(@NotNull Memorando entity) {
        if (entity.getStatus().equals(MemorandoStatus.PUBLISH)) {
            if (entity.getCreateAt() == null) entity.setCreateAt(Instant.now());
            if (entity.getNumber() == null) entity.setNumber(paramService.newInternalControl());
        }
    }

    public void addMyDepartment(@NotNull Memorando entity) {
        User me = userService.authenticate();

        if (entity.getFromDepartments().stream().noneMatch(x -> x.getId().equals(me.getPosition().getId()))) {
            entity.getFromDepartments().addFirst(me.getPosition());
        }
    }

    public void removeNotifications(@NotNull Memorando entity) {
        List<Notification> notifications = notificationService.findByReferenceIdAndType(entity.getId(), NotificationType.MEMORANDO);
        for (Notification notification : notifications) {
            notificationService.delete(notification.getId(), notification.getUser().getId());
        }
    }

    public void checkChanges(@NotNull MemorandoInsertDto dto, @NotNull Memorando entity) {
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
