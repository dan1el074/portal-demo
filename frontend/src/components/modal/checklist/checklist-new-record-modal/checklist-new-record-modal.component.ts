import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, FormSelectDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective } from '@coreui/angular';
import { Checklist, Template } from '../../../../app/interface/checklist.interface';
import { ChecklistPreviewService } from '../../../../app/services/checklist-preview.service';
import { ModalBackNavigationDirective } from '../../../../app/directive/modal-back-navigation.directive';

@Component({ selector: 'app-checklist-new-record-modal', imports: [CommonModule, FormsModule, ButtonCloseDirective, ButtonDirective, FormSelectDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective, ModalBackNavigationDirective], templateUrl: './checklist-new-record-modal.component.html', styleUrl: './checklist-new-record-modal.component.scss', changeDetection: ChangeDetectionStrategy.OnPush })
export class ChecklistNewRecordModalComponent {
  @Output() createRecord = new EventEmitter<{ templateId: string; predecessorId: string }>();
  protected visible = false; protected categoryId = ''; protected templateId = ''; protected predecessorId = '';
  constructor(protected store: ChecklistPreviewService, private cdr: ChangeDetectorRef) {}
  public open(): void { this.categoryId = this.store.state().categories.find(item => item.active && this.store.canOperate(item.id))?.id ?? ''; this.selectDefaultTemplate(); this.visible = true; this.cdr.detectChanges(); }
  protected close(): void { this.visible = false; this.cdr.detectChanges(); }
  protected categoryChanged(): void { this.selectDefaultTemplate(); }
  protected templateChanged(): void { this.predecessorId = ''; }
  protected get templates(): Template[] { const models = this.store.state().templates.filter(item => item.categoryId === this.categoryId && (!item.predecessorId || !item.automatic)); for (const record of this.store.state().records) { if (!this.store.visible(record) || record.status !== 'Finalizado' || this.store.blocked(record)) continue; const flow = this.store.flow(record); const index = flow.plan.findIndex(step => step.template.id === record.templateId); const next = flow.plan.slice(index + 1).find(step => !flow.skipped[step.template.id]); if (next?.category.id === this.categoryId && !next.template.automatic && !this.store.state().records.some(item => item.flowId === flow.id && item.templateId === next.template.id) && !models.some(item => item.id === next.template.id)) models.push(next.template); } return models; }
  protected get needsPredecessor(): boolean { return !!this.templates.find(item => item.id === this.templateId)?.predecessorId; }
  protected get predecessors(): Checklist[] { return this.store.state().records.filter(record => { if (!this.store.visible(record) || record.status !== 'Finalizado' || this.store.blocked(record)) return false; const flow = this.store.flow(record); const index = flow.plan.findIndex(step => step.template.id === record.templateId); const next = flow.plan.slice(index + 1).find(step => !flow.skipped[step.template.id]); return next?.template.id === this.templateId && !next.template.automatic && !this.store.state().records.some(item => item.flowId === flow.id && item.templateId === this.templateId); }); }
  private selectDefaultTemplate(): void { this.templateId = this.templates[0]?.id ?? ''; this.predecessorId = ''; }
  protected confirm(): void { if (!this.templateId || (this.needsPredecessor && !this.predecessorId)) return; this.createRecord.emit({ templateId: this.templateId, predecessorId: this.predecessorId }); this.close(); }
}
