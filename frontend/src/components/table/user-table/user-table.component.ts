import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AvatarComponent, ButtonDirective, ContainerComponent, ModalToggleDirective, PlaceholderAnimationDirective, PlaceholderDirective } from '@coreui/angular';
import { cilSearch, cilPencil, cilX } from '@coreui/icons';
import { UserTable } from '../../../app/interface/user.interface';
import { UserDeleteModalComponent } from '../../modal/user-delete-modal/user-delete-modal.component';
import { environment } from '../../../environments/environment';

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
    ButtonDirective,
    ModalToggleDirective,
    UserDeleteModalComponent,
    PlaceholderDirective,
    PlaceholderAnimationDirective
  ],
  templateUrl: './user-table.component.html',
  styleUrl: './user-table.component.scss',
})
export class UserTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data: Array<UserTable> = [];
  @Input() noMargin: boolean = false;
  @Input() hideDeactiveButton: boolean = false;
  @Output() updateUser = new EventEmitter<number>();
  @Output() deactivateTask = new EventEmitter<number>();

  protected apiUrl = environment.apiUrl;
  protected displayedColumns: string[] = ['name', 'username', 'position', 'email', 'updateAt', 'activated'];
  protected dataSource = new MatTableDataSource<UserTable>([]);
  protected icons = { cilSearch, cilPencil, cilX };
  protected loadSearch = true;

  constructor (private cdr: ChangeDetectorRef) {}

  ngOnChanges(): void {
    this.loadSearch = true;

    if (this.data) {
      this.dataSource.data = this.data;
    }

    setTimeout(() => {
      this.loadSearch = false;
      this.cdr.detectChanges();
    }, 500);
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.dataSource.filter = value.trim().toLowerCase();
  }

  editUser(id: number): void {
    this.updateUser.emit(id);
  }

  deactivateUser(id: number) {
    this.deactivateTask.emit(id);
  }
}
