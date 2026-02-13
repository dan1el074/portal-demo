import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from '../../../../../config/authGuard';
import { CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { InternalCommunicationService } from '../../../../../services/internal-communication.service';
import { InternalCommunication, NewInternalCommunication } from '../../../../../interface/internal-communication.interface';
import { CiEditFormComponent } from './../../../../../../components/forms/ci-edit-form/ci-edit-form.component';

@Component({
  selector: 'app-edit-ci',
  imports: [
    CommonModule,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    CiEditFormComponent
  ],
  templateUrl: './edit-ci.component.html',
  styleUrl: './edit-ci.component.scss',
})
export class EditCiComponent implements OnInit {
  protected isAdmin: boolean = false;
  protected editData: InternalCommunication = {
    id: 0,
    number: 0,
    request: 0,
    client: '',
    item: '',
    title: '',
    description: '',
    reason: '',
    createAt: '',
    user: {
      id: 0,
      name: '',
      position: {
        id: 0,
        name: ''
      },
      picture: null
    },
    interactions: [],
    interactionsSummary: [],
    fromDepartments: [],
    status: '',
    logs: []
  };

  public constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ciService: InternalCommunicationService,
    private toasterService: ToastrService,
    private authGuardService: AuthGuard,
    private cdr: ChangeDetectorRef
  ) { }

  public ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.authGuardService.getUser().subscribe({
      next: user => {
        if (user.roles.findIndex(role => role.authority == 'ROLE_ADMIN') >= 0) this.isAdmin = true;

        this.ciService.findById(id).subscribe({
          next: (data: InternalCommunication) => {
            this.editData = data;
            this.cdr.detectChanges();

            if (this.editData.status == 'CANCELED' || this.editData.status == 'APPROVED') {
              this.toasterService.error('Não é possível editar CIs aprovadas ou cancelas!');
              this.router.navigateByUrl('/general/internal-communication');
            }
            if (this.editData.status == 'PUBLISH' && !this.isAdmin) {
              this.toasterService.error('Apenas administradores podem alterar CIs já publicadas!');
              this.router.navigateByUrl('/general/internal-communication');
              return;
            }
          },
          error: (error) => {
            if (error.status == 401) {
              this.router.navigate(['login']);
              this.toasterService.error('Sessão expirada!');
              return;
            }

            this.toasterService.error('Registro não encontrado!');
            this.router.navigateByUrl('/general/internal-communication');
            return;
          }
        });
      },
      error: () => this.toasterService.error('Erro ao buscar informações do usuário!')
    });
  }

  protected editTask(data: NewInternalCommunication): void {
    this.ciService.update(this.editData.id, data).subscribe({
      next: () => {
        this.toasterService.success('CI atualizada com sucesso!');
        this.router.navigateByUrl('general/internal-communication/' + this.editData.id);
      },
      error: () => {
        this.toasterService.error('Erro ao atualizar CI!');
      }
    });
  }
}
