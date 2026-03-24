import { ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { MemorandoService } from './../../../../services/memorando.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Interaction, InteractionList, Memorando, NewMemorando } from '../../../../interface/memorando.interface';
import { CommonModule } from '@angular/common';
import { Position } from '../../../../interface/position.interface';
import { AuthGuard } from '../../../../config/authGuard';
import { Me } from '../../../../interface/user.interface';

@Component({
  selector: 'app-document-view',
  imports: [
    CommonModule,
    RowComponent,
    ColComponent,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    ButtonDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    RouterLink
  ],
  templateUrl: './document-view.component.html',
  styleUrl: './document-view.component.scss',
})
export class DocumentViewComponent implements OnInit {
  protected item: Memorando = {
    id: 0,
    number: 0,
    request: 0,
    client: '',
    items: [],
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
  protected user!: Me;
  protected interactions: Array<InteractionList> = [];
  protected isAdmin: boolean = false;
  protected canSign: boolean = false;
  protected showSignModal: boolean = false;
  protected showPublishModal: boolean = false;
  protected showCancelModal: boolean = false;
  protected showDeleteModal: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private memorandoService: MemorandoService,
    private toasterService: ToastrService,
    private authGuardService: AuthGuard,
    private cdr: ChangeDetectorRef
  ) { }

  public ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.authGuardService.getUser().subscribe(user => {
      if (user.roles.findIndex(role => role.authority == 'ROLE_ADMIN') >= 0) {
        this.user = user;
        this.isAdmin = true;
      }

      this.memorandoService.findById(id).subscribe({
        next: (data: Memorando) => {
          this.item = data;
          this.verifyCanSign(user);
          this.updateInteractions();
          this.cdr.detectChanges();
        },
        error: (error) => {
          if (error.status == 401) {
            this.router.navigate(['login']);
            this.toasterService.error('Sessão expirada!');
            return;
          }

          this.toasterService.error('Registro não encontrado!');
          this.router.navigate(['general/memorando']);
          return;
        }
      });
    });
  }

  private verifyCanSign(user: Me): void {
    // TODO: validar quem pode ou não assinar, somente gestores?

    if (this.item.status == "PUBLISH") {
      this.item.fromDepartments.forEach(currentDepartment => {
        if (currentDepartment.name == user.position) {
          this.canSign = true;
          return;
        }
      });

      if (this.canSign) {
        this.item.interactions.forEach(interaction => {
          if (interaction.departmentSigned.name == user.position) {
            this.canSign = false;
            return;
          }
        });
      }
    }
  }

  protected updateInteractions(): void {
    this.interactions = [];

    this.item.fromDepartments.forEach((department: Position) =>{
      let findInteraction = false;

      this.item.interactions.forEach((interaction: Interaction) => {
        if (department.id == interaction.departmentSigned.id) {
          findInteraction = true;

          this.interactions.push({
            check: true,
            position: department.name,
            signedBy: interaction.user.name
          });
          return;
        }
      })

      if (!findInteraction) {
        this.interactions.push({
          check: false,
          position: department.name,
          signedBy: null
        });
      }
    })
  }

  protected toggleSignModal() {
    this.showSignModal = !this.showSignModal;
  }

  protected handleSignModalChange(event: any) {
    this.showSignModal = event;
  }

  protected onSign(): void {
    this.memorandoService.sign(this.item.id).subscribe({
      next: (data: Memorando) => {
        this.item = data;
        this.canSign = false;
        this.updateInteractions();
        this.toasterService.success('CI assinada com sucesso!');
        this.toggleSignModal();
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao assinada CI!');
      }
    });
  }

  protected togglePublishModal() {
    this.showPublishModal = !this.showPublishModal;
  }

  protected handlePublishModalChange(event: any) {
    this.showPublishModal = event;
  }

  protected onPublish(): void {
    let departmentsId: Array<number> = [];
    this.item.fromDepartments.forEach(department => departmentsId.push(department.id));

    const memorando: NewMemorando = {
      request: this.item.request,
      client: this.item.client,
      items: this.item.items,
      title: this.item.title,
      description: this.item.description,
      reason: this.item.reason,
      departments: departmentsId,
      status: 'PUBLISH'
    };

    this.memorandoService.update(this.item.id, memorando).subscribe({
      next: (data: Memorando) => {
        this.item = data;
        this.updateInteractions();
        this.toasterService.success('CI publicada com sucesso!');
        this.togglePublishModal();
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao atualizar CI!');
      }
    });
  }

  protected toggleCancelModal() {
    this.showCancelModal = !this.showCancelModal;
  }

  protected handleCancelModalChange(event: any) {
    this.showCancelModal = event;
  }

  protected onCancel(): void {
    this.memorandoService.disable(this.item.id).subscribe({
      next: (data: Memorando) => {
        this.item = data;
        this.toasterService.success('CI cancelada com sucesso!');
        this.toggleCancelModal();
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao cancelar CI!');
      }
    });
  }

  protected toggleDeleteModal(status = !this.showDeleteModal) {
    this.showDeleteModal = status;
  }

  protected handleDeleteModalChange(event: any) {
    this.showDeleteModal = event;
  }

  protected onDelete(): void {
    this.memorandoService.delete(this.item.id).subscribe({
      next: () => {
        this.toggleDeleteModal(false);
        this.cdr.detectChanges();
        this.toasterService.success('CI deletada com sucesso!');
        this.router.navigateByUrl('general/memorando');
      },
      error: () => {
        this.toasterService.error('Erro ao deletar CI!');
      }
    });
  }
}
