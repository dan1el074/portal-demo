import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  ButtonCloseDirective,
  ButtonDirective,
  ModalBodyComponent,
  ModalComponent,
  ModalFooterComponent,
  ModalHeaderComponent,
  ModalTitleDirective,
} from '@coreui/angular';

@Component({
  selector: 'app-unsaved-changes-step-flow-modal',
  imports: [
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
  ],
  templateUrl: './unsaved-changes-step-flow-modal.component.html',
})
export class UnsavedChangesStepFlowModalComponent {
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  protected onClose(): void {
    this.close.emit();
  }
}
