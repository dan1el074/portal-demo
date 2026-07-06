import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, SpinnerComponent } from '@coreui/angular';
import { StepFlowService } from '../../../../app/services/step-flow.service';
import { StepFlowOrderInfo } from '../../../../app/interface/step-flow.interface';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-step-flow-modal',
  imports: [
    CommonModule,
    ModalComponent,
    ModalTitleDirective,
    ModalHeaderComponent,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
    ButtonCloseDirective,
    ReactiveFormsModule,
    FormsModule,
    FormLabelDirective,
    FormControlDirective,
    SpinnerComponent
  ],
  templateUrl: './new-step-flow-modal.component.html',
  styleUrl: './new-step-flow-modal.component.scss',
})
export class NewStepFlowModalComponent {
  @Input() visible!: boolean;
  @Output() closeModal = new EventEmitter<void>();
  @Output() createNewOrder = new EventEmitter<number>();

  protected newOrderForm: FormGroup;
  protected valid: boolean | undefined = undefined;
  protected searchResult: StepFlowOrderInfo | null = null;
  protected loadSearch: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private stepFlowService: StepFlowService,
    private toaster: ToastrService,
    private cdf: ChangeDetectorRef
  ) {
    this.newOrderForm = this.formBuilder.group({
      number: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
    });
  }

  public ngOnChanges(): void {
    if (!this.visible) {
      this.resetForm();
    }
  }

  protected closeMemorandoModal(): void {
    this.closeModal.emit();
    this.resetForm();
  }

  protected handleLiveDemoChange(event: any) {
    if (!event) this.closeMemorandoModal();
  }

  private resetForm(): void {
    this.newOrderForm.reset({ orderNumber: '' });
    this.valid = undefined;
    this.searchResult = null;
    this.loadSearch = false;
  }

  protected onSearch(): void {
    if (!this.newOrderForm.valid) {
      this.valid = false;
      return;
    }

    this.loadSearch = true;
    this.searchResult = null;

    this.stepFlowService.findOrderInfoByNumber(Number(this.newOrderForm.get('number')?.value)).subscribe({
      next: (data: StepFlowOrderInfo) => {
        this.searchResult = data;
        this.loadSearch = false;
        this.cdf.detectChanges();
      },
      error: () => {
        this.toaster.error("Erro ao buscar Ordem")
        this.loadSearch = false;
        this.cdf.detectChanges();
      },
    });
  }

  protected onCreate(): void {
    if (!this.newOrderForm.valid) {
      this.valid = false;
      return;
    }

    this.createNewOrder.emit(this.searchResult?.number);
  }
}
