import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, ModalToggleDirective } from '@coreui/angular';
import { cilPencil } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { UserTable } from '../../../../app/interface/user.interface';

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
  changeDetection: ChangeDetectionStrategy.Eager,
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
