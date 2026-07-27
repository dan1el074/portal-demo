import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  ButtonCloseDirective,
  ButtonDirective,
  ModalBodyComponent,
  ModalComponent,
  ModalFooterComponent,
  ModalHeaderComponent,
  ModalTitleDirective,
  SpinnerComponent,
} from '@coreui/angular';

export interface DeletableStepFlowMedia {
  id: number;
  name: string;
  type: 'image' | 'video';
}

@Component({
  selector: 'app-delete-step-flow-media-modal',
  imports: [
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
    SpinnerComponent,
  ],
  templateUrl: './delete-step-flow-media-modal.component.html',
  styleUrl: './delete-step-flow-media-modal.component.scss',
})
export class DeleteStepFlowMediaModalComponent {
  @Input() visible = false;
  @Input() media: DeletableStepFlowMedia | null = null;
  @Input() deleting = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirmDelete = new EventEmitter<void>();

  protected get mediaTypeLabel(): string {
    return this.media?.type === 'video' ? 'vídeo' : 'imagem';
  }

  protected onClose(): void {
    if (!this.deleting) {
      this.close.emit();
    }
  }

  protected onConfirm(): void {
    if (!this.deleting) {
      this.confirmDelete.emit();
    }
  }
}
