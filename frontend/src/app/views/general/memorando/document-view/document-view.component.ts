import { ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { MemorandoService } from './../../../../services/memorando.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Signature, SignatureList, Memorando, NewMemorando } from '../../../../interface/memorando.interface';
import { CommonModule } from '@angular/common';
import { cilPrint } from '@coreui/icons';
import { Position } from '../../../../interface/position.interface';
import { AuthGuard } from '../../../../config/authGuard';
import { Me } from '../../../../interface/user.interface';
import { NotificationWebSocketService } from '../../../../services/websocket.service';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-document-view',
  imports: [
    CommonModule,
    IconDirective,
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
  @ViewChild('printSection', { static: false }) printSection!: ElementRef;
  protected user!: Me;
  protected icons = { cilPrint };
  protected signatures: Array<SignatureList> = [];
  protected isAdmin: boolean = false;
  protected canSign: boolean = false;
  protected showSignModal: boolean = false;
  protected showRollbackModal: boolean = false;
  protected showPublishModal: boolean = false;
  protected showCancelModal: boolean = false;
  protected showDeleteModal: boolean = false;
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
    signature: [],
    signatureSummary: [],
    fromDepartments: [],
    status: '',
    logs: []
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private websocket: NotificationWebSocketService,
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
          this.updateSignatures();
          this.cdr.detectChanges();
        },
        error: () => {
          this.toasterService.error('Registro não encontrado!');
          this.router.navigate(['general/memorando']);
          return;
        }
      });
    });
  }

  protected printPage(): void {
    window.print();
  }

  private verifyCanSign(user: Me): void {
    if (this.item.status != "PUBLISH") return;

    this.item.fromDepartments.forEach(currentDepartment => {
      currentDepartment.manangers.forEach(mananger => {
        if (mananger.id == user.id) {
          this.canSign = true;
          return;
        }
     })
    });

    if (this.canSign) {
      this.item.signature.forEach(signature => {
        if (signature.user.id == user.id) {
          this.canSign = false;
          return;
        }
      });
    }
  }

  protected updateSignatures(): void {
    this.signatures = [];

    this.item.fromDepartments.forEach((department: Position) =>{
      let findSignature = false;
      let countSignatures = 0;

      this.item.signature.forEach((signature: Signature) => {
        if (department.id == signature.departmentSigned.id) {
          findSignature = true;
          countSignatures++;

          let index = this.signatures.findIndex(s => s.position == department.name);
          if (index != -1) {
            this.signatures[index].signedBy += "; " + signature.user.name;
            return;
          }

          this.signatures.push({
            check: false,
            position: department.name,
            signedBy: signature.user.name
          });
          return;
        }
      })

      if (department.manangers.length == countSignatures) {
        this.signatures[this.signatures.findIndex(s => s.position == department.name)].check = true;
      }

      if (!findSignature) {
        this.signatures.push({
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
        this.websocket.removeByReference(this.item.id);
        this.canSign = false;
        this.updateSignatures();
        this.toasterService.success('Memorando assinada com sucesso!');
        this.toggleSignModal();
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao assinar Memorando!');
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
        this.updateSignatures();
        this.toasterService.success('Memorando publicada com sucesso!');
        this.togglePublishModal();
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao atualizar Memorando!')
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
        this.toasterService.success('Memorando cancelado com sucesso!');
        this.toggleCancelModal();
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao cancelar Memorando!')
    });
  }

  protected toggleRollbackModal() {
    this.showRollbackModal = !this.showRollbackModal;
  }

  protected handleRollbackModalChange(event: any) {
    this.showRollbackModal = event;
  }

  protected onRollback(): void {
    this.memorandoService.rollback(this.item.id).subscribe({
      next: (data: Memorando) => {
        this.item = data;
        this.updateSignatures();
        this.toasterService.success('Memorando reiniciado com sucesso!');
        this.toggleRollbackModal();
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao reiniciar Memorando!')
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
        this.toasterService.success('Memorando deletado com sucesso!');
        this.router.navigateByUrl('general/memorando');
      },
      error: () => this.toasterService.error('Erro ao deletar Memorando!')
    });
  }
}
