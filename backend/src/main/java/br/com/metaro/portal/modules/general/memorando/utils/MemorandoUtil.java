package br.com.metaro.portal.modules.general.memorando.utils;

import br.com.metaro.portal.core.dto.notification.NotificationDto;
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
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import br.com.metaro.portal.modules.general.memorando.services.MemorandoLogService;
import br.com.metaro.portal.util.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private MemorandoRepository memorandoRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private EmailService emailService;

    public void dtoToEntity(MemorandoInsertDto dto, Memorando entity) {
        entity.setRequest(dto.getRequest());
        entity.setClient(dto.getClient());
        entity.setItems(new ArrayList<>(dto.getItems()));
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());

        List<Position> positions = positionRepository.findAllById(dto.getDepartments());
        if (positions.size() != dto.getDepartments().size()) {
            throw new UnprocessableEntityException("Um ou mais departamentos não foram encontrados!");
        }

        entity.setFromDepartments(new ArrayList<>(positions));
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

    public void addAllSignatures(Memorando entity) {
        entity.getSignatures().clear();
        memorandoRepository.flush();

        for (Position department : entity.getFromDepartments()) {
            for (User mananger : department.getManangers()) {
                entity.getSignatures().addLast(new Signature(entity, false, mananger, department));
            }
        }
    }

    public void publishPipeline(Memorando entity) {
        if (entity.getSignatures().isEmpty()) {
            throw new UnprocessableEntityException("Não foram encontradas assinaturas pendentes!");
        }

        addNumberAndCreatedAt(entity);

        logService.create(entity.getId(), "Publicou o documento nº %d/%d".formatted(entity.getNumber(),
                entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

        signAll(entity);
        notifyAllManangers(entity);
    }

    public void signAll(Memorando entity) {
        User me = userService.authenticate();

        for (Signature signature : entity.getSignatures()) {
            if (!signature.getUser().equals(me)) continue;
            if (signature.getIsSign()) continue;

            if (signature.getDepartmentSigned().getManangers().stream().noneMatch(u -> u.equals(me))) {
                throw new UnprocessableEntityException("Somente gestores podem assinar um Memorando!");
            }

            signature.setIsSign(true);
            logService.create(entity.getId(), "Assinou o documento (%s)"
                    .formatted(signature.getDepartmentSigned().getName()));
        }
    }

    public void notifyAllManangers(Memorando entity) {
        User me = userService.authenticate();

        for (Signature signature : entity.getSignatures()) {
            if (signature.getUser().equals(me)) continue;

            notificationService.create("Memorando nº %d - %s".formatted(entity.getNumber(), entity.getTitle()),
                    "/general/memorando/%d".formatted(entity.getId()), false, NotificationType.MEMORANDO,
                    entity.getId(), me, signature.getUser());
        }
    }

    public void notifyAllDepartmentUsers(Memorando entity) throws Exception {
        for (Position department : entity.getFromDepartments()) {
            for (User user : department.getUsers()) {
                notificationService.create("Memorando nº %d, com seu setor envolvido, foi finalizado!"
                        .formatted(entity.getNumber()), "/general/memorando/%d".formatted(entity.getId()),
                        true, NotificationType.MEMORANDO_FINISH, entity.getId(), null, user);

                DateTimeFormatter fullDateFormattter = DateTimeFormatter.ofPattern("dd/MM/yyyy '·' HH:mm")
                        .withZone(ZoneId.systemDefault());
                DateTimeFormatter yearFormattter = DateTimeFormatter.ofPattern("yyyy")
                        .withZone(ZoneId.systemDefault());

                String formatedDate = fullDateFormattter.format(Instant.now());
                String year = yearFormattter.format(entity.getCreateAt());

                emailService.sendHtmlEmail(user.getEmail(), "Memorando %d/%s finalizado"
                        .formatted(entity.getNumber(), year), """
                            <html lang="pt-BR">
                              <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <title>Relatório BI – Indicadores Operacionais</title>
                                <style>
                                  @import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600&display=swap');
                                  body, table, td, a {
                                    -webkit-text-size-adjust: 100%%;
                                    -ms-text-size-adjust: 100%%;
                                  }
                                  img { -ms-interpolation-mode: bicubic; border: 0; outline: none; text-decoration: none; }
                                  body { margin: 0; padding: 0; background-color: #f0f4f9; font-family: 'IBM Plex Sans', Arial, sans-serif; }
                                  .wrapper {
                                    width: 100%%;
                                    background-color: #f0f4f9;
                                    padding: 32px 0;
                                  }
                                  .container {
                                    max-width: 680px;
                                    margin: 0 auto;
                                    background-color: #ffffff;
                                    border-radius: 8px;
                                    overflow: hidden;
                                    border: 1px solid #d0dde8;
                                  }
                                  .header {
                                    background-color: #0f4f8a;
                                    padding: 28px 36px;
                                  }
                                  .header-subtitle {
                                    margin-top: 6px;
                                    color: #93bde0;
                                    font-size: 13px;
                                    font-weight: 400;
                                  }
                                  .title-band {
                                    background-color: #1565a8;
                                    padding: 16px 36px;
                                    border-bottom: 3px solid #0f4f8a;
                                  }
                                  .report-title {
                                    color: #ffffff;
                                    font-size: 17px;
                                    font-weight: 600;
                                    margin: 0;
                                  }
                                  .report-period {
                                    color: #aacceb;
                                    font-size: 12px;
                                    font-weight: 400;
                                    margin-top: 3px;
                                  }
                                  .main-section {
                                    padding: 28px 36px 20px;
                                    border-bottom: 1px solid #e1eaf3;
                                  }
                                  .text {
                                    font-size: 15px;
                                    line-height: 1.7;
                                    margin: 0 0 18px;
                                  }
                                  .info-card {
                                    margin: 28px 0;
                                    background-color: #f4f8fd;
                                    border: 1px solid #d0dde8;
                                    border-radius: 8px;
                                    overflow: hidden;
                                  }
                                  .info-header {
                                    background-color: #e8f1fa;
                                    padding: 14px 20px;
                                    border-bottom: 1px solid #d0dde8;
                                    font-size: 12px;
                                    font-weight: 600;
                                    color: #1565a8;
                                    text-transform: uppercase;
                                    letter-spacing: 0.8px;
                                  }
                                  .info-body {
                                    padding: 24px 20px;
                                  }
                                  .info-row {
                                    margin-bottom: 18px;
                                  }
                                  .info-label {
                                    display: block;
                                    font-size: 11px;
                                    color: #557799;
                                    font-weight: 600;
                                    text-transform: uppercase;
                                    margin-bottom: 5px;
                                    letter-spacing: 0.5px;
                                  }
                                  .info-value {
                                    font-size: 17px;
                                    font-weight: 600;
                                    color: #0f4f8a;
                                  }
                                  .badge {
                                    display: inline-block;
                                    padding: 7px 14px;
                                    border-radius: 999px;
                                    background-color: #d4efdf;
                                    color: #1a5c35;
                                    font-size: 12px;
                                    font-weight: 600;
                                  }
                                  .button-wrapper {
                                    margin-top: 34px;
                                    text-align: center;
                                  }
                                  .button {
                                    display: inline-block;
                                    background-color: #1565a8;
                                    color: #ffffff !important;
                                    text-decoration: none;
                                    padding: 14px 26px;
                                    border-radius: 6px;
                                    font-size: 14px;
                                    font-weight: 600;
                                  }
                                  .main-grid {
                                    display: grid;
                                    grid-template-columns: repeat(3, 1fr);
                                    gap: 12px;
                                  }
                                  .main-card {
                                    background-color: #f4f8fd;
                                    border: 1px solid #d0dde8;
                                    border-radius: 6px;
                                    padding: 14px 16px;
                                    text-align: center;
                                  }
                                  .main-label {
                                    font-size: 11px;
                                    color: #557799;
                                    font-weight: 500;
                                    margin-bottom: 6px;
                                  }
                                  .main-value {
                                    font-size: 24px;
                                    font-weight: 600;
                                    color: #0f4f8a;
                                    line-height: 1;
                                  }
                                  .main-unit {
                                    font-size: 11px;
                                    color: #557799;
                                    margin-top: 4px;
                                  }
                                  .main-delta {
                                    font-size: 11px;
                                    font-weight: 600;
                                    margin-top: 5px;
                                  }
                                  .main-delta.up { color: #1a7a3f; }
                                  .main-delta.down { color: #c0392b; }
                                  .main-delta.neutral { color: #557799; }
                                  .footer {
                                    background-color: #0f4f8a;
                                    padding: 20px 36px;
                                    text-align: center;
                                  }
                                  .footer-top {
                                    display: flex;
                                    justify-content: space-between;
                                    align-items: center;
                                    border-bottom: 1px solid #1e6eb5;
                                    padding-bottom: 14px;
                                    margin-bottom: 14px;
                                  }
                                  .footer-brand {
                                    color: #93bde0;
                                    font-size: 13px;
                                    font-weight: 500;
                                  }
                                  .footer-timestamp {
                                    color: #6699bb;
                                    font-size: 11px;
                                  }
                                  .footer-note {
                                    color: #6699bb;
                                    font-size: 11px;
                                    line-height: 1.6;
                                  }
                                  .divider {
                                    height: 3px;
                                    background: linear-gradient(90deg, #0f4f8a 0%%, #3b9edd 50%%, #0f4f8a 100%%);
                                  }
                                  @media only screen and (max-width: 620px) {
                                    .container { border-radius: 0; }
                                    .header, .main-section, .table-section, .footer, .title-band { padding-left: 20px; padding-right: 20px; }
                                    .main-grid { grid-template-columns: repeat(2, 1fr); }
                                  }
                                </style>
                              </head>
                              <body>
                                <div class="wrapper">
                                  <div class="container">
                                    <div class="header">
                                      <table width="100%%" cellpadding="0" cellspacing="0">
                                        <tr>
                                          <td>
                                            <img src="https://www.metaro.com.br/images/full-white-logo.svg" width="130" alt="Portal Metaro" style="display:block;border:0;outline:none;text-decoration:none;max-width:100%%;height:auto;" >
                                            <div class="header-subtitle">Memorando · Notificação Automática</div>
                                          </td>
                                          <td align="right" valign="middle">
                                            <div style="width:42px; height:42px; border-radius:8px; background:#1565a8; text-align:center; line-height:42px; color:#ffffff; font-size:22px; font-weight:bold;">
                                              ✓
                                            </div>
                                          </td>
                                        </tr>
                                      </table>
                                    </div>
                                    <div class="title-band">
                                      <div class="report-title">Memorando Finalizado com Sucesso</div>
                                      <div class="report-period">%s</div>
                                    </div>
                                    <div class="main-section">
                                      <p class="text">Olá,</p>
                                      <p class="text">
                                        Informamos que o <strong>memorando %d/%s</strong> foi finalizado com sucesso, estamos notificando você porque faz parte de uma das áreas envolvidas.
                                      </p>
                                      <div class="info-card">
                                        <div class="info-header">Dados do Memorando</div>
                                        <div class="info-body">
                                          <div class="info-row">
                                            <span class="info-label">Número</span>
                                            <span class="info-value">%d/%s</span>
                                          </div>
                                          <div class="info-row">
                                            <span class="info-label">Status</span>
                                            <span class="badge">APROVADO</span>
                                          </div>
                                          <div class="info-row">
                                            <span class="info-label">Título</span>
                                            <span class="info-value">%s</span>
                                          </div>
                                          <div class="info-row">
                                            <span class="info-label">Pedido</span>
                                            <span class="info-value">%d</span>
                                          </div>
                                          <div class="info-row" style="margin-bottom:0;">
                                            <span class="info-label">Cliente</span>
                                            <span class="info-value">%s</span>
                                          </div>
                                        </div>
                                      </div>
                                      <p class="text">
                                        Caso necessário, acesse o portal para consultar os detalhes, histórico e assinaturas relacionados ao memorando.
                                      </p>
                                      <div class="button-wrapper">
                                        <a href="http://portal.metaro.com.br" class="button">Acessar Portal</a>
                                      </div>
                                    </div>
                                    <div class="divider"></div>
                                    <div class="footer">
                                      <table width="100%%" cellpadding="0" cellspacing="0">
                                        <tr>
                                          <td align="left">
                                            <span class="footer-brand">Portal Metaro</span>
                                          </td>
                                          <td align="right">
                                            <span class="footer-timestamp">%s</span>
                                          </td>
                                        </tr>
                                      </table>
                                      <div style="border-top: 1px solid #1e6eb5; margin: 14px 0;"></div>
                                      <div class="footer-note">
                                        Este e-mail é gerado automaticamente, não responda!<br>
                                        Em caso de dúvidas, entre em contato com um administrador: ti@metaro.com.br
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </body>
                            </html>
                        """.formatted(formatedDate, entity.getNumber(), year, entity.getNumber(), year,
                                entity.getTitle(), entity.getRequest(), entity.getClient(), formatedDate));
            }
        }
    }

    public void checkIfEveryoneHasSigned(Memorando entity) throws Exception {
        if (entity.getSignatures().stream().noneMatch(s -> s.getIsSign().equals(false))) {
            entity.setStatus(MemorandoStatus.APPROVED);

            logService.system(entity.getId(), "Documento nº %d/%d aprovado por todas as áreas\n"
                    .formatted(entity.getNumber(), entity.getCreateAt().atZone(ZoneId.systemDefault()).getYear()));

            notifyAllDepartmentUsers(entity);
        }
    }

    public void addMyDepartment(Memorando entity) {
        User me = userService.authenticate();

        if (entity.getFromDepartments().stream().noneMatch(x -> x.getId().equals(me.getPosition().getId()))) {
            entity.getFromDepartments().addFirst(me.getPosition());
        }
    }

    public void checkIfAllDepartmentsAreActive(Memorando entity) {
        int count = 0;
        String departments = "";

        for (Position position : entity.getFromDepartments()) {
            if (position.getActivated().equals(false)) {
                count++;

                if (count == 1) {
                    departments = position.getName();
                    continue;
                }

                departments = departments.concat(", %s".formatted(position.getName()));
            }
        }

        if (count == 1) {
            throw new UnprocessableEntityException("O departamento %s está desativado!".formatted(departments));
        }
        if (count > 1) {
            throw new UnprocessableEntityException("Os seguintes departamentos estão desativados: %s".formatted(departments));
        }
    }

    public void addNumberAndCreatedAt(Memorando entity) {
        if (entity.getCreateAt() == null) entity.setCreateAt(Instant.now());
        if (entity.getNumber() == null) entity.setNumber(paramService.newInternalControl());
    }

    public void removeNotifications(Memorando entity) {
        // TODO: N + 1
        List<Notification> notifications = notificationService.findByReferenceIdAndType(entity.getId(), NotificationType.MEMORANDO);
        for (Notification notification : notifications) {
            notificationService.delete(notification.getId(), notification.getUser().getId());
        }
    }

    public void removeUserNotification(Long memorandoId, Long userId) {
        // TODO: N + 1

        for (NotificationDto notification : notificationService.listByUser(userId)) {
            if (
                notification.getType().equals(NotificationType.MEMORANDO.name())
                && notification.getReferenceId().equals(memorandoId)
            ) {
                notificationService.delete(notification.getId(), userId);
            }
        }
    }

    public void checkChanges(MemorandoInsertDto dto, Memorando entity) {
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

    private void diffPositions(MemorandoInsertDto dto, Memorando entity) {
        List<Position> positions = positionRepository.findAllById(dto.getDepartments());

        if (positions.size() != dto.getDepartments().size()) {
            throw new ResourceNotFoundException();
        }

        List<Position> addPositions = positions.stream().filter(currentPosition ->
                !entity.getFromDepartments().contains(currentPosition)).toList();

        List<Position> leftPositions = entity.getFromDepartments().stream().filter(currentPosition ->
                !positions.contains(currentPosition)).toList();

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

    private void diffItems(MemorandoInsertDto dto, Memorando entity) {
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
