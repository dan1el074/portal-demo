import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CardBodyComponent, CardComponent, ContainerComponent } from '@coreui/angular';
import { UserConfigFormComponent } from '../../../components/forms/user/user-config-form/user-config-form.component';
import { UserConfigData } from '../../interface/user.interface';
import { UserService } from '../../services/user.service';
import { ToastrService } from '@app/services/toast.service';
import { CommonModule } from '@angular/common';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { ErrorService } from '../../services/error.service';
import { HomeService } from '../../services/home.service';
import { RawMaterialsService } from '../../services/raw-materials.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-config',
  imports: [
    CommonModule,
    FormsModule,
    NgxSpinnerModule,
    CardComponent,
    CardBodyComponent,
    ContainerComponent,
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
}
