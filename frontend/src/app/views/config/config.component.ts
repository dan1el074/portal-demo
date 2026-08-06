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

@Component({
  selector: 'app-config',
  imports: [
    CommonModule,
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

  constructor(
    private userService: UserService,
    private homeService: HomeService,
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
}
