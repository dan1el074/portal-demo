import { CommonModule, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, ViewEncapsulation } from '@angular/core';
import {
  AccordionButtonDirective,
  AccordionComponent,
  AccordionItemComponent,
  TemplateIdDirective,
} from '@coreui/angular';
import { StepFlowOrder } from '../../../../app/interface/step-flow.interface';

@Component({
  selector: 'app-step-flow-history',
  imports: [
    CommonModule,
    NgTemplateOutlet,
    AccordionComponent,
    AccordionItemComponent,
    AccordionButtonDirective,
    TemplateIdDirective,
  ],
  templateUrl: './step-flow-history.component.html',
  styleUrl: './step-flow-history.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class StepFlowHistoryComponent {
  @Input({ required: true }) order!: StepFlowOrder;
}
