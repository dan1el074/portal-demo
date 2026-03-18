import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, ModalToggleDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilPencil } from '@coreui/icons';
import { Position } from '../../../app/interface/position.interface';

@Component({
  selector: 'app-position-delete-modal',
  imports: [
    ButtonDirective,
    ModalToggleDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    IconDirective
  ],
  templateUrl: './position-delete-modal.component.html',
  styleUrl: './position-delete-modal.component.scss',
})
export class PositionDeleteModalComponent {
  @Input() position!: Position;
  @Output() deactivateTask = new EventEmitter<number>();
  protected icons = { cilPencil };

  deactivateUser(): void {
    this.deactivateTask.emit(this.position.id);
  }
}
