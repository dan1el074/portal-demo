import { CommonModule, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IColumn, SmartTableComponent, TemplateIdDirective } from '@coreui/angular-pro';
import {
  getRawMaterialStockStatus,
  RawMaterialCategory,
  RawMaterialsTable,
  RawMaterialStockStatus,
  RawMaterialView,
} from '../../../app/interface/raw-materials.interface';

registerLocaleData(localePt);

@Component({
  selector: 'app-raw-materials-table',
  imports: [CommonModule, FormsModule, SmartTableComponent, TemplateIdDirective],
  templateUrl: './raw-materials-table.component.html',
  styleUrl: './raw-materials-table.component.scss',
})
export class RawMaterialsTableComponent implements OnChanges {
  @Input() data: RawMaterialsTable[] = [];
  @Input() categories: RawMaterialCategory[] = [];
  @Input() loading = false;
  @Input() mode: RawMaterialView = 'consultation';
  @Input() searchValue = '';
  @Input() selectedCategory = '';
  @Input() selectedStatus: RawMaterialStockStatus = 'all';
  @Input() showInactive = false;

  @Output() openItem = new EventEmitter<RawMaterialsTable>();
  @Output() sorterChange = new EventEmitter<any>();
  @Output() itemsPerPageChange = new EventEmitter<number>();
  @Output() filterChange = new EventEmitter<string>();
  @Output() categoryChange = new EventEmitter<string>();
  @Output() statusChange = new EventEmitter<RawMaterialStockStatus>();
  @Output() inactiveChange = new EventEmitter<boolean>();
  @Output() clearAll = new EventEmitter<void>();

  protected columns: (IColumn | string)[] = [];

  ngOnChanges(): void {
    const common: IColumn[] = [
      this.column('code', '#'),
      this.column('name', 'Descrição'),
    ];

    if (this.mode === 'operator') {
      common.push(
        this.column('currentStorage', 'Estoque'),
        this.column('type', 'Categoria'),
      );
    } else {
      common.push(
        this.column('type', 'Categoria'),
        this.column('currentStorage', 'Estoque'),
      );
    }

    if (this.mode !== 'operator') {
      common.push(
        this.column('currentStorageKg', 'Estoque (kg)'),
        this.column('minStorage', 'Estoque min'),
        this.column('minStorageKg', 'Estoque min (kg)'),
        this.column('maxStorage', 'Estoque max'),
        this.column('maxStorageKg', 'Estoque max (kg)'),
      );
    }

    common.push(this.column('updateAt', 'Modificado'));
    this.columns = common;
  }

  protected getStatus(item: RawMaterialsTable): Exclude<RawMaterialStockStatus, 'all'> {
    return getRawMaterialStockStatus(item);
  }

  protected onRowClick(event: { item: RawMaterialsTable }): void {
    if (this.mode === 'operator' && this.showInactive) return;
    this.openItem.emit(event.item);
  }

  protected onClearAll(): void {
    this.selectedCategory = '';
    this.selectedStatus = 'all';
    this.showInactive = false;
    this.clearAll.emit();
  }

  private column(key: string, label: string, sorter = true): IColumn {
    return {
      key,
      label,
      _labelTemplateId: 'all',
      _style: {
        backgroundColor: this.mode === 'operator'
          ? 'var(--cui-primary)'
          : 'rgba(var(--cui-emphasis-color-rgb), 0.04)',
        color: this.mode === 'operator' ? '#fff' : 'inherit',
        whiteSpace: 'nowrap',
      },
      sorter: sorter ? () => 0 : false,
      filter: false,
    };
  }
}
