import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StepFlowVideo } from '../../../../app/interface/step-flow.interface';
import { SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-video-modal',
  imports: [
    CommonModule
  ],
  templateUrl: './video-modal.component.html',
  styleUrl: './video-modal.component.scss',
})
export class VideoModalComponent {
  @Input() visible = false;
  @Input() video: (StepFlowVideo & { safeUrl: SafeResourceUrl }) | null = null;
  @Output() close = new EventEmitter<void>();

  protected onClose(): void {
    this.close.emit();
  }

  protected onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
