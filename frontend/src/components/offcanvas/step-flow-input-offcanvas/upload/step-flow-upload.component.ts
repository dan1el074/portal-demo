import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { ButtonDirective } from '@coreui/angular';
import { UploadedFile } from '../../../../app/interface/step-flow.interface';

@Component({
  selector: 'app-step-flow-upload',
  imports: [CommonModule, ButtonDirective],
  templateUrl: './step-flow-upload.component.html',
  styleUrl: './step-flow-upload.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class StepFlowUploadComponent {
  @Input() acceptedAllExtensions = '';
  @Input() isMobileDevice = false;
  @Input() isDragOver = false;
  @Input() files: UploadedFile[] = [];
  @Input() hasFiles = false;
  @Input() editingFileId: string | null = null;
  @Input() editingFileName = '';
  @Input() editingFileExtension = '';

  @Output() dragOver = new EventEmitter<DragEvent>();
  @Output() dragLeave = new EventEmitter<DragEvent>();
  @Output() filesDropped = new EventEmitter<DragEvent>();
  @Output() fileSelected = new EventEmitter<Event>();
  @Output() startFileNameEdit = new EventEmitter<UploadedFile>();
  @Output() fileNameInput = new EventEmitter<Event>();
  @Output() saveFileNameEdit = new EventEmitter<void>();
  @Output() cancelFileNameEdit = new EventEmitter<void>();
  @Output() removeFile = new EventEmitter<string>();
}
