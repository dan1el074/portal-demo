import { AlertComponent, ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { MemorandoService } from './../../../../services/memorando.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SignatureList, Memorando, NewMemorando, UpdateDepartmentMemorando } from '../../../../interface/memorando.interface';
import { CommonModule } from '@angular/common';
import { cilPrint } from '@coreui/icons';
import { AuthGuard } from '../../../../config/authGuard';
import { Me, UserSummary } from '../../../../interface/user.interface';
import { NotificationWebSocketService } from '../../../../services/websocket.service';
import { IconDirective } from '@coreui/icons-angular';
import { Position } from '../../../../interface/position.interface';

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
    RouterLink,
    AlertComponent
  ],
  templateUrl: './document-view.component.html',
  styleUrl: './document-view.component.scss',
})
export class DocumentViewComponent implements OnInit {
  @ViewChild('printSection', { static: false }) printSection!: ElementRef;
  protected user!: Me;
  protected icons = { cilPrint };
  protected signatures: Array<SignatureList> = [];
  protected signaturesMissing: Array<UserSummary> = [];
  protected isAdmin: boolean = false;
  protected canSign: boolean = false;
  protected showSignModal: boolean = false;
  protected showRollbackModal: boolean = false;
  protected showPublishModal: boolean = false;
  protected showCancelModal: boolean = false;
  protected showDeleteModal: boolean = false;
  protected showUpdateDepartmentModal: boolean = false;
  protected manangerAlert!: { user: UserSummary, department: Position } | null;
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
    signatures: [],
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
          this.updateSignatures();
          this.verifyCanSign(user);
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

  protected updateSignatures(): void {
    this.signatures = [];
    this.signaturesMissing = [];

    // adiciona departamentos
    this.item.fromDepartments.forEach(department => {
      this.signatures.push({
        check: false,
        position: department.name,
        signedBy: "",
      });
    })

    // adiciona assinaturas
    this.item.signatures.forEach(signature => {
      if (!signature.isSign) return;

      let index = this.signatures.findIndex(s => s.position == signature.departmentSigned.name);
      this.signatures[index].signedBy += this.signatures[index].signedBy ? '; ' + signature.user.name : signature.user.name;
    })

    // verificar assinaturas
    this.item.fromDepartments.forEach(department => {
      let allSignatures = this.item.signatures.filter(s => s.departmentSigned.name == department.name);
      let okSignatures = allSignatures.filter(s => s.isSign)

      if (allSignatures.length == okSignatures.length) {
        this.signatures[this.signatures.findIndex(s => s.position == department.name)].check = true;
        return;
      }

      allSignatures.filter(s => !s.isSign).forEach(missing => {
        if (this.signaturesMissing.findIndex(s => s.id == missing.user.id) == -1) {
          this.signaturesMissing.push(missing.user);
        }
      })
    })

    // verifica se alguém do "signaturesMissing" não é o gestor da área
    this.signaturesMissing.forEach(userMissing => {
      let find = false;

      for (let i=0; i<this.item.fromDepartments.length; i++) {
        for (let j=0; j<this.item.fromDepartments[i].manangers.length; j++) {
          if (this.item.fromDepartments[i].manangers[j].id == userMissing.id) {
            find = true;
            break;
          }
        }

        if (find) break;
      }

      if (!find) {
        let position = this.item.signatures.find(s => s.user.id == userMissing.id)?.departmentSigned;
        this.manangerAlert = { user: userMissing, department: position as Position };
      }
    })
  }

  private verifyCanSign(user: Me): void {
    if (this.item.status != "PUBLISH") return;

    if (
      this.signaturesMissing.find(u => u.id == user.id)
      && this.manangerAlert?.user.id != user.id
    ) {
      this.canSign = true;
    }
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
        this.toasterService.success('Memorando assinado com sucesso!');
        this.toggleSignModal();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.toasterService.error(error.error.error);
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
      error: (error) => {
        this.toasterService.error(error.error.error);
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
        this.toasterService.success('Memorando cancelado com sucesso!');
        this.toggleCancelModal();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.toasterService.error(error.error.error);
      }
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
        this.manangerAlert = null;
        this.updateSignatures();
        this.toasterService.success('Memorando reiniciado com sucesso!');
        this.toggleRollbackModal();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.toasterService.error(error.error.error);
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
        this.toasterService.success('Memorando deletado com sucesso!');
        this.router.navigateByUrl('general/memorando');
      },
      error: (error) => {
        this.toasterService.error(error.error.error);
      }
    });
  }

  protected toggleUpdateDepartmentModal(status = !this.showUpdateDepartmentModal) {
    this.showUpdateDepartmentModal = status;
  }

  protected handleUpdateDepartmentModalChange(event: any) {
    this.showUpdateDepartmentModal = event;
  }

  protected onUpdateDepartment(): void {
    let data: UpdateDepartmentMemorando = {
      userId: this.manangerAlert?.user.id as number,
      departmentId: this.manangerAlert?.department.id as number
    }

    this.memorandoService.updateSignatures(this.item.id, data).subscribe({
      next: (data: Memorando) => {
        this.item = data;
        this.manangerAlert = null;
        this.updateSignatures();
        this.verifyCanSign(this.user);
        this.toggleUpdateDepartmentModal(false);
        this.cdr.detectChanges();
        this.toasterService.success('Memorando atualizado com sucesso!');
      },
      error: (error) => {
        this.toasterService.error(error.error.error);
      }
    });
  }
}
