import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ButtonDirective,
  CardBodyComponent,
  CardComponent,
  ContainerComponent,
  DropdownComponent,
  DropdownItemDirective,
  DropdownItemPlainDirective,
  DropdownMenuDirective,
  DropdownToggleDirective,
  FormControlDirective,
} from '@coreui/angular';
import { finalize } from 'rxjs';
import { TrelloIntegrationOffcanvasComponent } from '../../../../components/offcanvas/trello-integration-offcanvas/trello-integration-offcanvas.component';
import { TrelloIntegrationTableComponent } from '../../../../components/table/trello-integration-table/trello-integration-table.component';
import {
  TrelloIntegrationRecord,
  TrelloIntegrationSettings,
  TrelloIntegrationSummary,
  TrelloIntegrationView,
} from '../../../interface/trello-integration.interface';
import { ToastrService } from '../../../services/toast.service';
import { ErrorService } from '../../../services/error.service';
import { TrelloIntegrationService } from '../../../services/trello-integration.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-trello-integration',
  imports: [
    CommonModule,
    FormsModule,
    ContainerComponent,
    CardComponent,
    CardBodyComponent,
    ButtonDirective,
    DropdownComponent,
    DropdownItemDirective,
    DropdownItemPlainDirective,
    DropdownMenuDirective,
    DropdownToggleDirective,
    FormControlDirective,
    TrelloIntegrationTableComponent,
    TrelloIntegrationOffcanvasComponent,
  ],
  templateUrl: './trello-integration.component.html',
  styleUrl: './trello-integration.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrelloIntegrationComponent implements OnInit {
  @ViewChild('recordOffcanvas') private recordOffcanvas!: TrelloIntegrationOffcanvasComponent;

  protected currentView: TrelloIntegrationView = 'operator';
  protected canSwitchViews = false;
  protected loadingTable = false;
  protected consultingErp = false;
  protected resendingEmail = false;
  protected loadingSettings = false;
  protected savingSettings = false;
  protected totalItems = 0;
  protected lastUpdatedAt?: Date;
  protected tableResetKey = 0;
  protected readonly views: Array<{ value: TrelloIntegrationView; label: string }> = [
    { value: 'operator', label: 'Operador' },
    { value: 'admin', label: 'Administrador' },
  ];
  protected settings: TrelloIntegrationSettings = {
    retentionDays: 90,
    erpLookbackDays: 7,
    destinationEmail: '',
    ccEmail: '',
  };
  protected records: TrelloIntegrationRecord[] = [];
  protected summary: TrelloIntegrationSummary = { total: 0, sent: 0, pending: 0, errors: 0 };

  constructor(
    private userService: UserService,
    private trelloIntegrationService: TrelloIntegrationService,
    private toasterService: ToastrService,
    private errorService: ErrorService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const roles = this.userService.getCurrentUser()?.roles.map(role => role.authority) ?? [];
    this.canSwitchViews = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_TRELLO_INTEGRATION_ADMIN');
    this.currentView = this.canSwitchViews ? 'admin' : 'operator';
    if (this.canSwitchViews) this.loadSettings();
    this.loadData();
  }

  protected setView(view: TrelloIntegrationView): void {
    if (!this.canSwitchViews) return;
    this.currentView = view;
  }

  protected viewLabel(): string {
    return this.currentView === 'admin' ? 'Administrador' : 'Operador';
  }

  protected consultErp(): void {
    if (this.consultingErp) return;

    this.consultingErp = true;
    this.trelloIntegrationService.consultErp().pipe(finalize(() => {
      this.consultingErp = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: result => {
        this.loadData();
        const details = [
          `${result.imported} importado(s)`,
          `${result.scheduled} e-mail(s) programado(s)`,
          `${result.ignored} ignorado(s)`,
        ].join(', ');
        this.toasterService.success(`Consulta concluída: ${details}.`, 'Consultar ERP');
      },
      error: error => this.errorService.showError(error),
    });
  }

  protected refreshData(): void {
    if (this.loadingTable) return;
    this.loadData();
  }

  protected saveSettings(): void {
    const retentionDays = Math.trunc(Number(this.settings.retentionDays));
    const erpLookbackDays = Math.trunc(Number(this.settings.erpLookbackDays));
    const destinationEmail = this.settings.destinationEmail.trim();
    const ccEmail = this.settings.ccEmail.trim();

    if (
      !Number.isFinite(retentionDays) || retentionDays < 1 || retentionDays > 3650 ||
      !Number.isFinite(erpLookbackDays) || erpLookbackDays < 1 || erpLookbackDays > 3650 ||
      !destinationEmail
    ) {
      this.toasterService.warning('Preencha os campos obrigatórios com valores válidos.');
      return;
    }

    this.savingSettings = true;
    this.trelloIntegrationService.updateSettings({
      retentionDays,
      erpLookbackDays,
      destinationEmail,
      ccEmail,
    }).subscribe({
      next: settings => {
        this.settings = settings;
        this.savingSettings = false;
        this.toasterService.success('Configurações da Integração Trello atualizadas.');
        this.cdr.detectChanges();
      },
      error: error => {
        this.savingSettings = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  private loadSettings(): void {
    this.loadingSettings = true;
    this.trelloIntegrationService.getSettings().subscribe({
      next: settings => {
        this.settings = settings;
        this.loadingSettings = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.loadingSettings = false;
        this.errorService.showError(error);
        this.cdr.detectChanges();
      },
    });
  }

  protected openRecord(record: TrelloIntegrationRecord): void {
    this.recordOffcanvas.open(record);
    this.trelloIntegrationService.findById(record.id).subscribe({
      next: current => this.recordOffcanvas.update(current),
      error: error => this.errorService.showError(error),
    });
  }

  protected resendEmail(record: TrelloIntegrationRecord): void {
    if (this.resendingEmail) return;

    this.resendingEmail = true;
    this.trelloIntegrationService.resend(record.id).pipe(finalize(() => {
      this.resendingEmail = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: updated => {
        this.recordOffcanvas.update(updated);
        this.loadSummary();
        if (updated.status === 'SENT') {
          this.toasterService.success(`E-mail do pedido ${updated.order}, item ${updated.code}, reenviado com sucesso.`);
        } else {
          this.toasterService.error(updated.errorMessage || 'Não foi possível reenviar o e-mail para o Trello.');
        }
      },
      error: error => {
        this.trelloIntegrationService.findById(record.id).subscribe({
          next: current => this.recordOffcanvas.update(current),
        });
        this.errorService.showError(error);
      },
    });
  }

  private loadData(): void {
    this.tableResetKey++;
    this.loadRecords();
    this.loadSummary();
  }

  private loadRecords(): void {
    this.loadingTable = true;
    this.trelloIntegrationService.findSnapshot().pipe(finalize(() => {
      this.loadingTable = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: records => {
        this.records = records;
        this.totalItems = records.length;
        this.lastUpdatedAt = new Date();
      },
      error: error => this.errorService.showError(error),
    });
  }

  private loadSummary(): void {
    this.trelloIntegrationService.getSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.cdr.detectChanges();
      },
      error: error => this.errorService.showError(error),
    });
  }

}
