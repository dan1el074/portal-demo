import { Component, EventEmitter, Input, Output} from '@angular/core';
import { IColumn, IItem, SmartTableComponent, TemplateIdDirective } from '@coreui/angular-pro';
import { TruncatePipe } from './../../../app/pipes/truncate.pipe';

@Component({
  selector: 'app-step-flow-table',
  imports: [
    SmartTableComponent,
    TemplateIdDirective,
    TruncatePipe
  ],
  templateUrl: './step-flow-table.component.html',
  styleUrl: './step-flow-table.component.scss',
})
export class StepFlowTableComponent {
  @Output() openInfoTask = new EventEmitter<number>();
  @Input() data!: Array<IItem>;

  protected columns: (IColumn | string)[] = [
    {
      key: 'order',
      label: '#',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)', whiteSpace: 'nowrap' }
    },
    {
      key: 'client',
      label: 'Cliente',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' }
    },
    {
      key: 'initialDate',
      label: 'Emissão',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' }
    },
    {
      key: 'finalDate',
      label: 'Prazo',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' }
    },
    {
      key: 'step',
      label: 'Etapa atual',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' }
    },
    {
      key: 'status',
      label: 'Status',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' }
    },
    {
      key: 'progress',
      label: 'Progresso',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      filter: false,
      sorter: false,
    }
  ];

  protected steps: Array<string> = ["Montagem Final", "PCP", "Frete", "Faturamento", "Expedição"];

  protected openInfo(event: any): void {
    this.openInfoTask.emit(event.item.id);
  }
}
