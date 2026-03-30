import { Component, OnInit } from '@angular/core';
import { CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent } from '@coreui/angular';
import { UserConfigFormComponent } from '../../../components/forms/user/user-config-form/user-config-form.component';
import { UserConfigData } from '../../interface/user.interface';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-config',
  imports: [
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
  protected userData!: UserConfigData;

  constructor(
    private userService: UserService,
    private toasterService: ToastrService
  ) {}

  public ngOnInit(): void {
    this.userService.getUserConfig().subscribe({
      next: data => this.userData = data,
      error: () => this.toasterService.error('Erro ao carregar dados do usuário!')
    });
  }

  public updateConfig(data: FormData): void {
    this.userService.updateConfig(data).subscribe({
      next: () => {
        this.userService.refreshUser().subscribe({
          next: () => this.toasterService.success('Configurações salvas com sucesso!'),
          error: () => this.toasterService.error('Erro inesperado, contate um administrador!')
        });
      },
      error: () => this.toasterService.error('Erro ao atualizar dados do usuário!')
    });
  }
}
