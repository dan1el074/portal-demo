import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { AvatarComponent } from '@coreui/angular';
import { IColumn, ISorterValue, SmartTableComponent, TemplateIdDirective, TooltipDirective } from '@coreui/angular-pro';
import { environment } from '../../../environments/environment';
import { MemorandoList, MemorandoStatus } from '../../../app/interface/memorando.interface';

@Component({
  selector: 'app-memorando-table',
  imports: [CommonModule, AvatarComponent, SmartTableComponent, TemplateIdDirective, TooltipDirective],
  templateUrl: './memorando-table.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './memorando-table.component.scss',
})
export class MemorandoTableComponent {
  @Input() data: Array<MemorandoList> = [];
  @Input() loading = false;
  @Input() searchValue = '';
  @Input() fullText = false;
  @Input() showStatusFilters = false;
  @Input() selectedStatus?: MemorandoStatus;
  @Input() sorterValue: { column?: string; state?: 'asc' | 'desc' } = {};
  @Output() openMemorandoTask = new EventEmitter<number>();
  @Output() sorterChange = new EventEmitter<any>();
  @Output() itemsPerPageChange = new EventEmitter<number>();
  @Output() filterChange = new EventEmitter<string>();
  @Output() fullTextChange = new EventEmitter<boolean>();
  @Output() statusChange = new EventEmitter<MemorandoStatus | undefined>();
  @Output() clearAll = new EventEmitter<void>();

  protected readonly apiUrl = environment.apiUrl;
  protected readonly columns: IColumn[] = [
    this.column('number', 'Número'),
    this.column('request', 'Pedido'),
    this.column('client', 'Cliente'),
    this.column('status', 'Status'),
    this.column('signatureSummary', 'Assinaturas', false),
    this.column('createAt', 'Criado'),
  ];

  protected openMemorando(event: { item: MemorandoList }): void {
    this.openMemorandoTask.emit(event.item.id);
  }

  protected onFilterValueChange(value: string): void {
    if (value === this.searchValue) return;
    this.filterChange.emit(value);
  }

  protected onSorterValueChange(sorter: ISorterValue): void {
    if (sorter?.column === this.sorterValue?.column && sorter?.state === this.sorterValue?.state) return;
    this.sorterChange.emit(sorter);
  }

  protected onFullTextChange(event: Event): void {
    this.fullTextChange.emit((event.target as HTMLInputElement).checked);
  }

  protected selectStatus(status?: MemorandoStatus): void {
    this.statusChange.emit(status);
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
