import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective } from '@coreui/angular';
import { ModalBackNavigationDirective } from '../../../../app/directive/modal-back-navigation.directive';
import { ChecklistPreviewService } from '../../../../app/services/checklist-preview.service';
import { ToastrService } from '../../../../app/services/toast.service';
import { ChecklistErpOrder, ChecklistService } from '../../../../app/services/checklist.service';

@Component({
  selector: 'app-checklist-update-order-modal',
  imports: [FormsModule, ButtonCloseDirective, ButtonDirective, FormControlDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective, ModalBackNavigationDirective],
  templateUrl: './checklist-update-order-modal.component.html',
  styleUrl: './checklist-update-order-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChecklistUpdateOrderModalComponent {
  @Output() updated = new EventEmitter<void>();
  protected visible = false;
  protected flowId = '';
  protected selectedOrder = '';
  protected selected?: ChecklistErpOrder;
  protected loading = false;

  constructor(protected store: ChecklistPreviewService, private api: ChecklistService, private toaster: ToastrService, private cdr: ChangeDetectorRef) {}

  public open(flowId: string): void {
    this.flowId = flowId;
    this.selectedOrder = '';
    this.selected = undefined;
    this.visible = true;
    this.cdr.detectChanges();
  }

  protected get flow() {
    return this.store.state().flows.find((item) => item.id === this.flowId);
  }

  protected search(): void {
    if (!this.selectedOrder.trim()) return;
    this.loading = true;
    this.api.findErpOrder(this.selectedOrder).subscribe({
      next: (order) => {
        const compatible = order.items.some((item) => this.flow?.item.startsWith(item.code));
        this.selected = compatible && String(order.number) !== this.flow?.order ? order : undefined;
        if (!this.selected) this.toaster.warning('O pedido não contém o mesmo item ou já é o pedido atual.');
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => { this.selected = undefined; this.loading = false; this.toaster.warning('Pedido não encontrado no ERP.'); this.cdr.detectChanges(); },
    });
  }

  protected close(): void {
    this.visible = false;
    this.cdr.detectChanges();
  }

  protected async save(): Promise<void> {
    try {
      if (!this.selected) return;
      await this.store.updateFlowOrder(this.flowId, this.selectedOrder);
      this.close();
      this.updated.emit();
    } catch (error) {
      this.toaster.warning(error instanceof Error ? error.message : 'Não foi possível atualizar o pedido.');
    }
  }
}
