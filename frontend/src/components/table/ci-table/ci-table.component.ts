import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, Input, OnChanges, ViewChild } from '@angular/core';
import { AvatarComponent, BadgeComponent, ButtonDirective, ContainerComponent, TooltipDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilSearch, cilPencil, cilX } from '@coreui/icons';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { environment } from '../../../environments/environment';
import { InternalCommunication } from '../../../app/interface/internal-communication.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-ci-table',
  imports: [
    CommonModule,
    BadgeComponent,
    TooltipDirective,
    AvatarComponent,
    ContainerComponent,
    IconDirective,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    ButtonDirective,
    RouterLink
  ],
  templateUrl: './ci-table.component.html',
  styleUrl: './ci-table.component.scss',
})
export class CiTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data!: Array<InternalCommunication>;
  @Input() noMargin: boolean = false;

  protected apiUrl = environment.apiUrl;
  protected displayedColumns: string[] = ['ciNumber', 'orderNumber', 'client', 'status', 'interaction', 'createdAt', 'buttons'];
  protected dataSource = new MatTableDataSource<InternalCommunication>([]);
  protected icons = { cilSearch, cilPencil, cilX };

  ngOnChanges(): void {
    if (this.data) {
      this.dataSource.data = this.data;
    }
  }

  applyFilter(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.dataSource.filter = value.trim().toLowerCase();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.dataSource.sortingDataAccessor = (item: InternalCommunication, property: string) => {
      switch (property) {
        case 'ciNumber': {
          const year = new Date(item.createAt).getFullYear();
          const numberPadded = item.number.toString().padStart(6, '0');
          return Number(`${year}${numberPadded}`);
        }

        case 'orderNumber':
          return item.request;

        case 'client':
          return item.client?.toLowerCase();

        case 'status':
          return item.status?.toLowerCase();

        case 'createdAt':
          return new Date(item.createAt);

        default:
          return (item as any)[property];
      }
    };
  }

}
