import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
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
import { ModalBackNavigationDirective } from '@app/directive/modal-back-navigation.directive';

export interface DeletableStepFlowMedia {
  id: number;
  name: string;
  type: 'image' | 'video';
}

@Component({
  selector: 'app-delete-step-flow-media-modal',
  imports: [
    ModalComponent,
    ModalBackNavigationDirective,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
    SpinnerComponent,
  ],
  templateUrl: './delete-step-flow-media-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
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
