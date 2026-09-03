import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { IColumn, SmartTableComponent, TemplateIdDirective } from '@coreui/angular-pro';
import { TrelloIntegrationRecord } from '../../../app/interface/trello-integration.interface';

@Component({
  selector: 'app-trello-integration-table',
  imports: [CommonModule, SmartTableComponent, TemplateIdDirective],
  templateUrl: './trello-integration-table.component.html',
  styleUrl: './trello-integration-table.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
})
export class TrelloIntegrationTableComponent implements OnChanges {
  @Input() data: TrelloIntegrationRecord[] = [];
  @Input() loading = false;
  @Input() resetKey = 0;
  @Output() openRecord = new EventEmitter<TrelloIntegrationRecord>();

  protected tableFilterValue = '';

  protected readonly columns: IColumn[] = [
    this.column('order', 'Pedido'),
    this.column('client', 'Cliente'),
    this.column('code', 'Código'),
    this.column('description', 'Descrição'),
    this.column('status', 'Status'),
    this.column('releaseDate', 'Liberação'),
  ];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['resetKey'] && !changes['resetKey'].firstChange) this.tableFilterValue = '';
  }

  protected open(event: { item: TrelloIntegrationRecord }): void {
    this.openRecord.emit(event.item);
  }

  private column(key: string, label: string, sorter = true): IColumn {
    return {
      key,
      label,
      _labelTemplateId: 'all',
      _style: { backgroundColor: 'rgba(var(--cui-emphasis-color-rgb), 0.04)', whiteSpace: 'nowrap' },
      sorter: sorter ? () => 0 : false,
      filter: false,
    };
  }
}
