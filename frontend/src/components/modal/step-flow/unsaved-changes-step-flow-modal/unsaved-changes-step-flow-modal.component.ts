import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import {
  ButtonCloseDirective,
  ButtonDirective,
  ModalBodyComponent,
  ModalComponent,
  ModalFooterComponent,
  ModalHeaderComponent,
  ModalTitleDirective,
} from '@coreui/angular';
import { ModalBackNavigationDirective } from '../../../../app/directive/modal-back-navigation.directive';

@Component({
  selector: 'app-unsaved-changes-step-flow-modal',
  imports: [
    ModalComponent,
    ModalBackNavigationDirective,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
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
