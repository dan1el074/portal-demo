import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ContainerComponent } from '@coreui/angular';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { UserConfigFormComponent } from '../../../components/forms/user/user-config-form/user-config-form.component';
import { UserConfigData } from '../../interface/user.interface';
import { ErrorService } from '../../services/error.service';
import { ToastrService } from '../../services/toast.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-config',
  imports: [
    NgxSpinnerModule,
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

  constructor(
    private userService: UserService,
    private toasterService: ToastrService,
    private errorService: ErrorService,
    private spinner: NgxSpinnerService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.spinner.show('userConfigSpinner');
    this.userService.getUserConfig().subscribe({
      next: data =>  {
        this.userData = data;
        this.loaded = true;
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
          error: error => this.toasterService.error(error.error.error)
        });
      },
      error: error => this.errorService.showError(error)
    });
  }
}
