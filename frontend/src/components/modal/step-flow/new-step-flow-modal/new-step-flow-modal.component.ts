import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, SpinnerComponent } from '@coreui/angular';
import { StepFlowService } from '../../../../app/services/step-flow.service';
import { StepFlowOrderInfo, StepFlowOrderItem } from '../../../../app/interface/step-flow.interface';
import { CommonModule } from '@angular/common';
import { TruncatePipe } from '../../../../app/pipes/truncate.pipe';

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
    SpinnerComponent,
    TruncatePipe
  ],
  templateUrl: './new-step-flow-modal.component.html',
  styleUrl: './new-step-flow-modal.component.scss',
})
export class NewStepFlowModalComponent {
  @Input() visible!: boolean;
  @Output() closeModal = new EventEmitter<void>();
  @Output() createNewOrder = new EventEmitter<StepFlowOrderInfo>();

  protected newOrderForm: FormGroup;
  protected itemsForm!: FormArray;
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

    this.itemsForm = this.formBuilder.array([]);
  }

  protected get itemsFormControls(): FormGroup[] {
    return this.itemsForm.controls as FormGroup[];
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
    this.itemsForm.clear();
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
    this.itemsForm.clear();

    this.stepFlowService.findOrderInfoByNumber(Number(this.newOrderForm.get('number')?.value)).subscribe({
      next: (data: StepFlowOrderInfo) => {
        this.searchResult = data;
        this.buildItemsForm(data.items);
        this.stopLoadButton();
      },
      error: error => {
        this.stopLoadButton();

        switch (error.status) {
          case 404:
            this.toaster.error("Pedido não encontrado!");
            break;

          case 422:
            this.toaster.error("Esse pedido já foi produzido!");
            break;

          default:
            this.toaster.error("Erro ao buscar Ordem");
            break;
        }
      }
    });
  }

  private buildItemsForm(items: Array<StepFlowOrderItem>): void {
    items.forEach(item => {
      const maxQuantity = item.quantity - item.producedQuantity;

      this.itemsForm.push(
        this.formBuilder.group({
          code: [item.code],
          description: [item.description],
          quantity: [item.quantity],
          maxQuantity: [maxQuantity],
          producedQuantity: [maxQuantity, [Validators.required, Validators.min(0), Validators.max(maxQuantity)],
          ],
        })
      );
    });
  }

  private stopLoadButton(): void {
    setTimeout(() => {
      this.loadSearch = false;
      this.cdf.detectChanges();
    }, 500);
  }

  protected onCreate(): void {
    if (!this.newOrderForm.valid || this.itemsForm.invalid) {
      this.itemsForm.markAllAsTouched();
      return;
    }

    const hasAtLeastOneItem = this.itemsForm.controls.some(
      control => control.get('producedQuantity')?.value > 0
    );

    if (!hasAtLeastOneItem) {
      this.toaster.error('Informe a quantidade de ao menos um item para continuar.');
      return;
    }

    const updatedItems: StepFlowOrderItem[] = this.searchResult!.items.map((item, index) => ({
      ...item,
      producedQuantity: this.itemsForm.at(index).get('producedQuantity')?.value,
    }));

    const updatedResult: StepFlowOrderInfo = { ...this.searchResult!, items: updatedItems };

    this.createNewOrder.emit(updatedResult);
  }
}
