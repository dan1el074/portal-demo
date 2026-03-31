import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, Input, OnChanges, ViewChild } from '@angular/core';
import { AvatarComponent, BadgeComponent, ButtonDirective, ContainerComponent, TooltipDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilSearch, cilPencil, cilX, cilExternalLink } from '@coreui/icons';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { environment } from '../../../environments/environment';
import { Memorando } from '../../../app/interface/memorando.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-memorando-table',
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
  templateUrl: './memorando-table.component.html',
  styleUrl: './memorando-table.component.scss',
})
export class MemorandoTableComponent implements AfterViewInit, OnChanges {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() data!: Array<Memorando>;
  @Input() noMargin: boolean = false;

  protected apiUrl = environment.apiUrl;
  protected displayedColumns: string[] = ['memorandoNumber', 'orderNumber', 'client', 'status', 'signature', 'createdAt', 'buttons'];
  protected dataSource = new MatTableDataSource<Memorando>([]);
  protected icons = { cilSearch, cilPencil, cilX, cilExternalLink };

  ngOnChanges(): void {
    if (this.data && this.data.length > 0) {
      this.data = this.data.sort((a, b) => {
        const aYear = new Date(a.createAt).getFullYear();
        const aNumberPadded = a.number.toString().padStart(6, '0');
        const aRes = Number(`${aYear}${aNumberPadded}`);

        const bYear = new Date(b.createAt).getFullYear();
        const bNumberPadded = b.number ? b.number.toString().padStart(6, '0') : "";
        const bRes = Number(`${bYear}${bNumberPadded}`);

        return bRes - aRes;
      });

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

    this.dataSource.sortingDataAccessor = (item: Memorando, property: string) => {
      switch (property) {
        case 'memorandoNumber': {
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
      };
    };
  }

}
