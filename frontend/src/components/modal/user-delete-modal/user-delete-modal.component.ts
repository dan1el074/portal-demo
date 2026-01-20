import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, ModalToggleDirective } from '@coreui/angular';
import { UserTable } from '../../../app/interface/user.interface';
import { cilPencil } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-user-delete-modal',
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
  templateUrl: './user-delete-modal.component.html',
  styleUrl: './user-delete-modal.component.scss',
})
export class UserDeleteModalComponent {
  @Input() user!: UserTable;
  @Output() deactivateTask = new EventEmitter<number>();
  protected icons = { cilPencil };

  deactivateUser(): void {
    this.deactivateTask.emit(this.user.id);
  }

}
