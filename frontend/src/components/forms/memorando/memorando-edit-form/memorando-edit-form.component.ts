import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective } from '@coreui/angular';
import { OrderInfo } from '../../../../app/interface/erp.interface';
import { Memorando, NewMemorando } from '../../../../app/interface/memorando.interface';
import { MemorandoService } from '../../../../app/services/memorando.service';
import { ToastrService } from 'ngx-toastr';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-memorando-edit-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    ButtonDirective,
    RouterLink
  ],
  templateUrl: './memorando-edit-form.component.html',
  styleUrl: './memorando-edit-form.component.scss',
})
export class MemorandoEditFormComponent implements OnChanges {
  @Input() data!: Memorando;
  @Input() isAdmin!: boolean;
  @Output() editTask = new EventEmitter<NewMemorando>();

  protected orderInfo: Array<OrderInfo> = [];
  protected valid: boolean | undefined;
  protected editForm: FormGroup;
  protected showErrors = false;

  constructor(
    private MemorandoService: MemorandoService,
    private toasterService: ToastrService,
    private router: Router,
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef,
  ) {
    this.editForm = this.formBuilder.group({
      request: ['', [Validators.required]],
      client: ['', [Validators.required, Validators.minLength(5)]],
      item: ['', [Validators.required]],
      title: ['', [Validators.required]],
      description: [ '', [Validators.required, Validators.minLength(5)]],
      reason: ['', [Validators.required, Validators.minLength(5)]],
      departments: ['', [Validators.required]],
      status: ['']
    })
  }

  public ngOnChanges(): void {
    if (this.data.id == 0) return;

    this.MemorandoService.searchOrder(this.data.request).subscribe({
      next: (data: Array<OrderInfo>) => {
        if (data.length == 0) {
          this.toasterService.error('Informações do pedido não encontradas!');
          this.toasterService.error('Erro ao consultar informações do pedido!');
          return;
        }

        this.orderInfo = data;
        this.updateForm();
        this.cdr.detectChanges();
      },
      error: () => {
        this.router.navigateByUrl('/general/memorando');
        this.toasterService.error('Erro ao consultar informações do pedido!');
      }
    });
  }

  private updateForm(): void {
    const departments: Array<number> = [];
    this.data.fromDepartments.forEach(department => departments.push(department.id));

    this.editForm.patchValue({
      request: this.orderInfo[0].number,
      client: this.orderInfo[0].client,
      item: this.data.item,
      title: this.data.title,
      description: this.data.description.replaceAll('<br>', '\n'),
      reason: this.data.reason,
      departments: departments.join(', ')
    });
  }

  protected onSave(): void {
    this.editForm.patchValue({
      status: 'CREATED'
    });
    this.onSubmit();
  }

  protected onPublish(): void {
    this.editForm.patchValue({
      status: 'PUBLISH'
    });
    this.onSubmit();
  }

  protected onSubmit(): void {
    if (!this.editForm.valid) {
      this.showErrors = true;
      return;
    }

    const data: NewMemorando = {...this.editForm.value};
    data.description = data.description.replace(/\r?\n/g, '<br>');
    this.editTask.emit(data);
  }
}
