import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from '../../../../../config/authGuard';
import { CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { MemorandoService } from '../../../../../services/memorando.service';
import { Memorando, NewMemorando } from '../../../../../interface/memorando.interface';
import { MemorandoEditFormComponent } from '../../../../../../components/forms/memorando/memorando-edit-form/memorando-edit-form.component';

@Component({
  selector: 'app-edit-memorando',
  imports: [
    CommonModule,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    MemorandoEditFormComponent
  ],
  templateUrl: './edit-memorando.component.html',
  styleUrl: './edit-memorando.component.scss',
})
export class EditMemorandoComponent implements OnInit {
  protected isAdmin: boolean = false;
  protected editData: Memorando = {
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
    private memorandoService: MemorandoService,
    private toasterService: ToastrService,
    private authGuardService: AuthGuard,
    private cdr: ChangeDetectorRef
  ) { }

  public ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.authGuardService.getUser().subscribe({
      next: user => {
        if (user.roles.findIndex(role => role.authority == 'ROLE_ADMIN') >= 0) this.isAdmin = true;

        this.memorandoService.findById(id).subscribe({
          next: (data: Memorando) => {
            this.editData = data;
            this.cdr.detectChanges();

            if (this.editData.status == 'CANCELED' || this.editData.status == 'APPROVED') {
              this.toasterService.error('Não é possível editar CIs aprovadas ou cancelas!');
              this.router.navigateByUrl('/general/memorando');
            }
            if (this.editData.status == 'PUBLISH' && !this.isAdmin) {
              this.toasterService.error('Apenas administradores podem alterar CIs já publicadas!');
              this.router.navigateByUrl('/general/memorando');
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
            this.router.navigateByUrl('/general/memorando');
            return;
          }
        });
      },
      error: () => this.toasterService.error('Erro ao buscar informações do usuário!')
    });
  }

  protected editTask(data: NewMemorando): void {
    this.memorandoService.update(this.editData.id, data).subscribe({
      next: () => {
        this.toasterService.success('CI atualizada com sucesso!');
        this.router.navigateByUrl('general/memorando/' + this.editData.id);
      },
      error: () => {
        this.toasterService.error('Erro ao atualizar CI!');
      }
    });
  }
}
