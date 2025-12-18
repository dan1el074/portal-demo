import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, Input, OnChanges, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AvatarComponent, ButtonDirective, ContainerComponent } from '@coreui/angular';
import { cilSearch, cilPencil, cilX } from '@coreui/icons';
import { UserTable } from '../../../app/interface/user.interface';

@Component({
  selector: 'app-user-table',
  imports: [
    CommonModule,
    AvatarComponent,
    ContainerComponent,
    IconDirective,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    ButtonDirective
  ],
  templateUrl: './user-table.component.html',
  styleUrl: './user-table.component.scss',
})
export class UserTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data: Array<UserTable> = [];

  protected displayedColumns: string[] = ['name', 'username', 'position', 'email', 'updateAt', 'activated'];
  protected dataSource = new MatTableDataSource<UserTable>([]);
  protected icons = { cilSearch, cilPencil, cilX };

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

}
