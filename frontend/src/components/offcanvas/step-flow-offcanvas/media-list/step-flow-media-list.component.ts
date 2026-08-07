import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StepFlowOrder, StepFlowVideo } from '../../../../app/interface/step-flow.interface';
import { getSortedStepFlowMedia, StepFlowMedia, StepFlowMediaFilter } from '../../../../app/interface/step-flow-media.interface';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-step-flow-media-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './step-flow-media-list.component.html',
  styleUrl: './step-flow-media-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class StepFlowMediaListComponent {
  @ViewChild('mediaSearchInput') mediaSearchInput?: ElementRef<HTMLInputElement>;
  @Input({ required: true }) order!: StepFlowOrder;
  @Output() openVideo = new EventEmitter<StepFlowVideo>();

  protected readonly apiUrl = environment.apiUrl;
  protected mediaFilter: StepFlowMediaFilter = 'all';
  protected mediaSearch = '';
  protected mediaSearchOpen = false;

  protected get filteredMedia(): StepFlowMedia[] {
    return getSortedStepFlowMedia(this.order.pictures, this.order.videos, this.mediaFilter, this.mediaSearch);
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
