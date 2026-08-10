import { CommonModule, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, ViewEncapsulation } from '@angular/core';
import { StepFlowOrder } from '../../../../app/interface/step-flow.interface';

@Component({
  selector: 'app-step-flow-timeline',
  imports: [CommonModule, NgTemplateOutlet],
  templateUrl: './step-flow-timeline.component.html',
  styleUrls: ['./step-flow-timeline.component.scss', './step-flow-timeline-responsive.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class StepFlowTimelineComponent {
  @Input({ required: true }) order!: StepFlowOrder;
}
