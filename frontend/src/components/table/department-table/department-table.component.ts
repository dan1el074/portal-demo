import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AvatarComponent, ButtonDirective, ContainerComponent, ModalToggleDirective, TooltipDirective } from '@coreui/angular';
import { cilSearch, cilPencil, cilX } from '@coreui/icons';
import { PositionTable } from '../../../app/interface/position.interface';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-department-table',
  imports: [
    CommonModule,
    ContainerComponent,
    IconDirective,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    ButtonDirective,
    ModalToggleDirective,
    TooltipDirective,
    AvatarComponent
  ],
  templateUrl: './department-table.component.html',
  styleUrl: './department-table.component.scss',
})
export class DepartmentTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data: Array<PositionTable> = [];
  @Input() noMargin: boolean = true;
  @Input() hideDeactiveButton: boolean = false;
  @Output() updatePosition = new EventEmitter<number>();
  @Output() deactivateTask = new EventEmitter<number>();

  protected apiUrl = environment.apiUrl;
  protected displayedColumns: string[] = ['id', 'name', 'managers', 'updatedAt', 'createdAt', 'buttons'];
  protected dataSource = new MatTableDataSource<PositionTable>([]);
  protected icons = { cilSearch, cilPencil, cilX };

  ngOnChanges(): void {
    if (this.data) {
      this.dataSource.data = this.data;
    }
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.dataSource.filter = value.trim().toLowerCase();
  }

  editPosition(id: number): void {
    // this.updatePosition.emit(id);
    alert('Editar!');
  }

  deactivatePosition(id: number) {
    // this.deactivateTask.emit(id);
    alert('Desativar!');
  }
}
