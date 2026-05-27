import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AvatarComponent, ButtonDirective, ContainerComponent, ModalToggleDirective, PlaceholderAnimationDirective, PlaceholderDirective, TooltipDirective } from '@coreui/angular';
import { cilSearch, cilPencil, cilX } from '@coreui/icons';
import { Position } from '../../../app/interface/position.interface';
import { environment } from '../../../environments/environment';
import { PositionDeleteModalComponent } from '../../modal/position-delete-modal/position-delete-modal.component';

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
    AvatarComponent,
    PositionDeleteModalComponent,
    PlaceholderDirective,
    PlaceholderAnimationDirective
  ],
  templateUrl: './department-table.component.html',
  styleUrl: './department-table.component.scss',
})
export class DepartmentTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data: Array<Position> = [];
  @Input() noMargin: boolean = true;
  @Input() hideDeactiveButton: boolean = false;
  @Output() updatePosition = new EventEmitter<number>();
  @Output() deactivePosition = new EventEmitter<number>();

  protected apiUrl = environment.apiUrl;
  protected displayedColumns: string[] = ['id', 'name', 'manangers', 'updatedAt', 'createdAt', 'buttons'];
  protected dataSource = new MatTableDataSource<Position>([]);
  protected icons = { cilSearch, cilPencil, cilX };
  protected loadSeach = true;

  constructor (private cdr: ChangeDetectorRef) {}

  ngOnChanges(): void {
    this.loadSeach = true;

    if (this.data) {
      this.dataSource.data = this.data;
    }

    setTimeout(() => {
      this.loadSeach = false;
      this.cdr.detectChanges();
    }, 800);
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
    this.updatePosition.emit(id);
  }

  deactivatePosition(id: number) {
    this.deactivePosition.emit(id);
  }
}
