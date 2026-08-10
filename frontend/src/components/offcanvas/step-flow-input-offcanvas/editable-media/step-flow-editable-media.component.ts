import { CommonModule, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StepFlowImage } from '../../../../app/interface/image.interface';
import { StepFlowOrder, StepFlowVideo, UploadingVideo } from '../../../../app/interface/step-flow.interface';
import { getSortedStepFlowMedia, matchesStepFlowMediaSearch, StepFlowMedia, StepFlowMediaFilter } from '../../../../app/interface/step-flow-media.interface';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-step-flow-editable-media',
  imports: [CommonModule, FormsModule, NgTemplateOutlet],
  templateUrl: './step-flow-editable-media.component.html',
  styleUrl: './step-flow-editable-media.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class StepFlowEditableMediaComponent {
  @ViewChild('mediaSearchInput') mediaSearchInput?: ElementRef<HTMLInputElement>;
  @Input({ required: true }) order!: StepFlowOrder;
  @Input() uploadingVideos: UploadingVideo[] = [];
  @Output() deleteImage = new EventEmitter<StepFlowImage>();
  @Output() deleteVideo = new EventEmitter<StepFlowVideo>();
  @Output() openVideo = new EventEmitter<StepFlowVideo>();

  protected readonly apiUrl = environment.apiUrl;
  protected mediaFilter: StepFlowMediaFilter = 'all';
  protected mediaSearch = '';
  protected mediaSearchOpen = false;

  protected get filteredMedia(): StepFlowMedia[] {
    return getSortedStepFlowMedia(this.order.pictures, this.order.videos, this.mediaFilter, this.mediaSearch);
  }

  protected get filteredUploadingVideos(): UploadingVideo[] {
    if (this.mediaFilter === 'image') return [];
    return this.uploadingVideos.filter(video => matchesStepFlowMediaSearch(video.name, this.mediaSearch));
  }

  protected setMediaFilter(filter: StepFlowMediaFilter): void {
    this.mediaFilter = filter;
  }

  protected toggleMediaSearch(): void {
    this.mediaSearchOpen = !this.mediaSearchOpen;
    if (!this.mediaSearchOpen) {
      this.mediaSearch = '';
      return;
    }
    setTimeout(() => this.mediaSearchInput?.nativeElement.focus());
  }

  protected onPreviewError(event: Event): void {
    (event.target as HTMLImageElement).style.display = 'none';
  }
}
