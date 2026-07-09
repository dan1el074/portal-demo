import { Component, EventEmitter, Input, Output} from '@angular/core';
import { IColumn, IItem, SmartTableComponent, TemplateIdDirective, TooltipDirective } from '@coreui/angular-pro';
import { TruncatePipe } from './../../../app/pipes/truncate.pipe';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-step-flow-table',
  imports: [
    SmartTableComponent,
    TemplateIdDirective,
    TruncatePipe,
    FormsModule,
    TooltipDirective
  ],
  templateUrl: './step-flow-table.component.html',
  styleUrl: './step-flow-table.component.scss',
})
export class StepFlowTableComponent {
  @Output() openInfoTask = new EventEmitter<number>();
  @Output() sorterChange = new EventEmitter<any>();
  @Output() itemsPerPageChange = new EventEmitter<any>();
  @Output() filterChange = new EventEmitter<string>();
  @Output() stepFilterChange = new EventEmitter<string>();
  @Output() clearAll = new EventEmitter<void>();
  @Input() data: Array<IItem> = [];
  @Input() loading: boolean = false;
  @Input() searchValue: string = '';
  @Input() selectedStep: string = '';

  protected steps: Array<string> = ["Montagem Final", "PCP", "Frete", "Faturamento", "Expedição"];
  protected columns: (IColumn | string)[] = [
    {
      key: 'number',
      label: '#',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)', whiteSpace: 'nowrap' },
      sorter: () => 0
    },
    {
      key: 'quantity',
      label: 'QTD',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      sorter: false
    },
    {
      key: 'client',
      label: 'Cliente',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      sorter: () => 0
    },
    {
      key: 'dueDate',
      label: 'Prazo',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      sorter: () => 0
    },
    {
      key: 'currentStep',
      label: 'Etapa atual',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      sorter: () => 0
    },
    {
      key: 'status',
      label: 'Status',
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)' },
      sorter: () => 0
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

  protected onStepChange(value: string): void {
    this.stepFilterChange.emit(value);
  }

  protected onClearAll(): void {
    this.selectedStep = '';
    this.clearAll.emit();
  }

  protected openInfo(event: any): void {
    this.openInfoTask.emit(event.item.id);
  }
}
