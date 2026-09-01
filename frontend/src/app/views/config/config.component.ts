import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ButtonCloseDirective, CardBodyComponent, CardComponent, ContainerComponent, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, SpinnerComponent } from '@coreui/angular';
import { UserConfigFormComponent } from '../../../components/forms/user/user-config-form/user-config-form.component';
import { ActiveSession, UserConfigData } from '../../interface/user.interface';
import { UserService } from '../../services/user.service';
import { ToastrService } from '../../services/toast.service';
import { CommonModule } from '@angular/common';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { ErrorService } from '../../services/error.service';
import { HomeService } from '../../services/home.service';
import { RawMaterialsService } from '../../services/raw-materials.service';
import { FormsModule } from '@angular/forms';
import { ModalBackNavigationDirective } from '../../directive/modal-back-navigation.directive';
import { EmailLog, EmailLogPage } from '../../interface/email-log.interface';
import { EmailLogService } from '../../services/email-log.service';
import { FoccoService } from '../../services/focco.service';
import { ProbusService } from '../../services/probus.service';

@Component({
  selector: 'app-config',
  imports: [
    CommonModule,
    FormsModule,
    NgxSpinnerModule,
    CardComponent,
    CardBodyComponent,
    ContainerComponent,
    ModalComponent,
    ModalBackNavigationDirective,
    ModalHeaderComponent,
    ModalTitleDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonCloseDirective,
    SpinnerComponent,
    UserConfigFormComponent
  ],
  templateUrl: './config.component.html',
  styleUrl: './config.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigComponent implements OnInit {
  @ViewChild(UserConfigFormComponent)

  protected userConfigForm!: UserConfigFormComponent;
  protected userData: UserConfigData | null = null;
  protected loaded = false;
  protected isAdmin = false;
  protected rawMaterialHistoryRetention = 1000;
  protected savingHistoryRetention = false;
  protected activeSessionsVisible = false;
  protected activeSessionsLoading = false;
  protected activeSessions: Array<ActiveSession> = [];
  protected activeSessionsError = '';
  protected disconnectingSessions = new Set<string>();
  protected emailLogsVisible = false;
  protected emailLogsLoading = false;
  protected emailLogsError = '';
  protected emailLogs: Array<EmailLog> = [];
  protected emailLogsPage = 0;
  protected emailLogsTotalPages = 0;
  protected emailLogsTotalElements = 0;
  private readonly emailLogsPageSize = 15;
  protected foccoKey = '';
  protected foccoToken = '';
  protected foccoTokenConfigured = false;
  protected foccoConfigLoading = false;
  protected foccoConfigSaving = false;
  protected probusJdbcUrl = '';
  protected probusUsername = '';
  protected probusPassword = '';
  protected probusPasswordConfigured = false;
  protected probusConfigLoading = false;
  protected probusConfigSaving = false;

  constructor(
    private userService: UserService,
    private homeService: HomeService,
    private rawMaterialsService: RawMaterialsService,
    private emailLogService: EmailLogService,
    private foccoService: FoccoService,
    private probusService: ProbusService,
    private toasterService: ToastrService,
    private errorService: ErrorService,
    private spinner: NgxSpinnerService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.spinner.show("userConfigSpinner");
    this.isAdmin = this.userService.getCurrentUser()?.roles.some(
      role => role.authority === 'ROLE_ADMIN'
    ) ?? false;
    if (this.isAdmin) {
      this.loadFoccoConfig();
      this.loadProbusConfig();
      this.rawMaterialsService.getHistoryRetention().subscribe(setting => {
        this.rawMaterialHistoryRetention = setting.value;
        this.cdr.detectChanges();
      });
    }

    this.userService.getUserConfig().subscribe({
      next: data =>  {
        this.userData = data;
        this.loaded = true
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao carregar dados do usuário!')
    });
  }

  protected saveFoccoConfig(): void {
    const key = this.foccoKey.trim();
    if (!key) {
      this.toasterService.error('Informe a Chave do FoccoERP.');
      return;
    }
    if (!this.foccoTokenConfigured && !this.foccoToken.trim()) {
      this.toasterService.error('Informe o token do FoccoERP.');
      return;
    }

    this.foccoConfigSaving = true;
    this.foccoService.updateConfig({ key, token: this.foccoToken }).subscribe({
      next: config => {
        this.foccoKey = config.key;
        this.foccoToken = '';
        this.foccoTokenConfigured = config.tokenConfigured;
        this.foccoConfigSaving = false;
        this.toasterService.success('Integração com o FoccoERP atualizada.');
        this.cdr.detectChanges();
      },
      error: error => {
        this.foccoConfigSaving = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  private loadFoccoConfig(): void {
    this.foccoConfigLoading = true;
    this.foccoService.getConfig().subscribe({
      next: config => {
        this.foccoKey = config.key;
        this.foccoTokenConfigured = config.tokenConfigured;
        this.foccoConfigLoading = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.foccoConfigLoading = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  protected saveProbusConfig(): void {
    const jdbcUrl = this.probusJdbcUrl.trim();
    const username = this.probusUsername.trim();
    if (!jdbcUrl) {
      this.toasterService.error('Informe a URL JDBC do Probus.');
      return;
    }
    if (!username) {
      this.toasterService.error('Informe o usuário do Probus.');
      return;
    }
    if (!this.probusPasswordConfigured && !this.probusPassword.trim()) {
      this.toasterService.error('Informe a senha do Probus.');
      return;
    }

    this.probusConfigSaving = true;
    this.probusService.updateConfig({ jdbcUrl, username, password: this.probusPassword }).subscribe({
      next: config => {
        this.probusJdbcUrl = config.jdbcUrl;
        this.probusUsername = config.username;
        this.probusPassword = '';
        this.probusPasswordConfigured = config.passwordConfigured;
        this.probusConfigSaving = false;
        this.toasterService.success('Integração com o Probus atualizada.');
        this.cdr.detectChanges();
      },
      error: error => {
        this.probusConfigSaving = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  private loadProbusConfig(): void {
    this.probusConfigLoading = true;
    this.probusService.getConfig().subscribe({
      next: config => {
        this.probusJdbcUrl = config.jdbcUrl;
        this.probusUsername = config.username;
        this.probusPasswordConfigured = config.passwordConfigured;
        this.probusConfigLoading = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.probusConfigLoading = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  public updateConfig(data: FormData): void {
    this.userService.updateConfig(data).subscribe({
      next: () => {
        this.userService.refreshUser().subscribe({
          next: () => {
            this.userConfigForm.clearPasswordInput();
            this.toasterService.success('Configurações salvas com sucesso!');
          },
          error: (error) => this.toasterService.error(error.error.error)
        });
      },
      error: (error) => this.errorService.showError(error)
    });
  }

  protected clearCache(): void {
    this.homeService.clearAllCache().subscribe({
      next: () => this.toasterService.success('Cache limpo com sucesso!'),
      error: () => this.toasterService.error('Erro ao limpar cache!')
    });
  }

  protected saveHistoryRetention(): void {
    const value = Math.trunc(Number(this.rawMaterialHistoryRetention));
    if (!Number.isFinite(value) || value < 10 || value > 100000) {
      this.toasterService.error('Informe um valor entre 10 e 100.000 registros.');
      return;
    }
    this.savingHistoryRetention = true;
    this.rawMaterialsService.updateHistoryRetention(value).subscribe({
      next: setting => {
        this.rawMaterialHistoryRetention = setting.value;
        this.savingHistoryRetention = false;
        this.toasterService.success('Retenção do histórico atualizada.');
        this.cdr.detectChanges();
      },
      error: error => {
        this.savingHistoryRetention = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  protected openActiveSessions(): void {
    this.activeSessionsVisible = true;
    this.activeSessionsLoading = true;
    this.activeSessions = [];
    this.activeSessionsError = '';
    this.userService.getActiveSessions().subscribe({
      next: sessions => {
        this.activeSessions = sessions;
        this.activeSessionsLoading = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.activeSessionsLoading = false;
        this.activeSessionsError = 'Não foi possível carregar as sessões ativas.';
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  protected closeActiveSessions(): void {
    this.activeSessionsVisible = false;
    this.activeSessions = [];
    this.activeSessionsError = '';
  }

  protected onActiveSessionsVisibleChange(visible: boolean): void {
    if (!visible) this.closeActiveSessions();
  }

  protected disconnectSession(session: ActiveSession): void {
    if (this.disconnectingSessions.has(session.sessionId)) return;
    this.disconnectingSessions.add(session.sessionId);
    this.userService.disconnectActiveSession(session.sessionId).subscribe({
      next: () => {
        this.activeSessions = this.activeSessions.filter(item => item.sessionId !== session.sessionId);
        this.disconnectingSessions.delete(session.sessionId);
        this.toasterService.success(`Sessão de ${session.username} desconectada.`);
        this.cdr.detectChanges();
      },
      error: error => {
        this.disconnectingSessions.delete(session.sessionId);
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  protected openEmailLogs(): void {
    this.emailLogsVisible = true;
    this.loadEmailLogs(0);
  }

  protected closeEmailLogs(): void {
    this.emailLogsVisible = false;
    this.emailLogs = [];
    this.emailLogsError = '';
  }

  protected onEmailLogsVisibleChange(visible: boolean): void {
    if (!visible) this.closeEmailLogs();
  }

  protected changeEmailLogsPage(page: number): void {
    if (page < 0 || page >= this.emailLogsTotalPages || page === this.emailLogsPage) return;
    this.loadEmailLogs(page);
  }

  private loadEmailLogs(page: number): void {
    this.emailLogsLoading = true;
    this.emailLogsError = '';
    this.emailLogService.list(page, this.emailLogsPageSize).subscribe({
      next: (result: EmailLogPage) => {
        this.emailLogs = result.content;
        this.emailLogsPage = result.number;
        this.emailLogsTotalPages = result.totalPages;
        this.emailLogsTotalElements = result.totalElements;
        this.emailLogsLoading = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.emailLogsLoading = false;
        this.emailLogsError = 'Não foi possível carregar o histórico de e-mails.';
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }
}
