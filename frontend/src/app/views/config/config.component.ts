import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ButtonCloseDirective, CardBodyComponent, CardComponent, ContainerComponent, ModalBodyComponent, ModalComponent, ModalHeaderComponent, ModalTitleDirective, SpinnerComponent } from '@coreui/angular';
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
  protected disconnectingSessions = new Set<number>();

  constructor(
    private userService: UserService,
    private homeService: HomeService,
    private rawMaterialsService: RawMaterialsService,
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
    if (this.disconnectingSessions.has(session.userId)) return;
    this.disconnectingSessions.add(session.userId);
    this.userService.disconnectActiveSession(session.userId).subscribe({
      next: () => {
        this.activeSessions = this.activeSessions.filter(item => item.userId !== session.userId);
        this.disconnectingSessions.delete(session.userId);
        this.toasterService.success(`Sessão de ${session.username} desconectada.`);
        this.cdr.detectChanges();
      },
      error: error => {
        this.disconnectingSessions.delete(session.userId);
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }
}
