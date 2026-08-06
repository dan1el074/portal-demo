import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective } from '@coreui/angular';
import { cilTrash } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-delete-post-modal',
  imports: [ModalComponent, ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent, ModalFooterComponent, ButtonDirective, ButtonCloseDirective, IconDirective],
  templateUrl: './delete-post-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './delete-post-modal.component.scss',
})
export class DeletePostModalComponent {
  @Output() closeTask = new EventEmitter<void>();
  @Output() deleteTask = new EventEmitter<number>();
  @Input() visible = false;
  @Input() id = 0;
  @Input() label = 'publicação';
  protected icons = { cilTrash };
  protected closeModal(): void { this.closeTask.emit(); }
  protected handleToggleModal(visible: boolean): void { if (!visible) this.closeModal(); }
  protected onDelete(id: number): void { this.deleteTask.emit(id); }
}
