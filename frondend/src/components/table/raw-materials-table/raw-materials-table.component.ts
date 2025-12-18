import { RawMaterialModalComponent } from './../../modal/raw-material-modal/raw-material-modal.component';
import { IconDirective } from '@coreui/icons-angular';
import { CommonModule, registerLocaleData  } from '@angular/common';
import { AfterViewInit, Component, Input, OnChanges, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ButtonDirective, ContainerComponent, ModalToggleDirective } from '@coreui/angular';
import { cilPencil, cilSearch } from '@coreui/icons';
import { RawMaterialsTable } from '../../../app/interface/raw-materials.interface';
import localePt from '@angular/common/locales/pt';

registerLocaleData(localePt);

@Component({
  selector: 'app-raw-materials-table',
  imports: [
    CommonModule,
    ContainerComponent,
    IconDirective,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    RawMaterialModalComponent,
    ModalToggleDirective,
    ButtonDirective
  ],
  templateUrl: './raw-materials-table.component.html',
  styleUrl: './raw-materials-table.component.scss',
})
export class RawMaterialsTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data: Array<RawMaterialsTable> = [];

  protected displayedColumns: string[] = [
    'code',
    'name',
    'currentStorage',
    'currentStorageKg',
    'minStorage',
    'minStorageKg',
    'maxStorage',
    'maxStorageKg',
    'updateAt',
    'activated'
  ];
  protected dataSource = new MatTableDataSource<RawMaterialsTable>([]);
  protected icons = { cilSearch, cilPencil };

  ngOnChanges() {
    if (this.data) {
      this.dataSource.data = this.data;
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.dataSource.filter = value.trim().toLowerCase();
  }

  getColor(item: RawMaterialsTable): string {
    if (item.currentStorage >= item.minStorage && item.currentStorage <= item.maxStorage) {
      return 'success';
    }

    if (item.currentStorage > item.maxStorage) {
      return 'warning';
    }

    return 'danger'
  }

}
