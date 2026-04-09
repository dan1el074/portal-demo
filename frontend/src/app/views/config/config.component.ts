import { CustomError, FieldMessage } from './../../interface/error.interface';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent } from '@coreui/angular';
import { UserConfigFormComponent } from '../../../components/forms/user/user-config-form/user-config-form.component';
import { UserConfigData } from '../../interface/user.interface';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { NgxSpinnerModule, NgxSpinnerService } from 'ngx-spinner';
import { ErrorService } from '../../services/error.service';

@Component({
  selector: 'app-config',
  imports: [
    CommonModule,
    NgxSpinnerModule,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    UserConfigFormComponent
  ],
  templateUrl: './config.component.html',
  styleUrl: './config.component.scss',
})
export class ConfigComponent implements OnInit {
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
    this.spinner.show("userConfigSpinner");

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
          next: () => this.toasterService.success('Configurações salvas com sucesso!'),
          error: (error) => this.toasterService.error(error.error.error)
        });
      },
      error: (error) => this.errorService.showError(error)
    });
  }
}
