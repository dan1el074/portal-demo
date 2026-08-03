import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { AvatarComponent, ButtonDirective, ModalToggleDirective } from '@coreui/angular';
import { IColumn, SmartTableComponent, TemplateIdDirective } from '@coreui/angular-pro';
import { IconDirective } from '@coreui/icons-angular';
import { cilX } from '@coreui/icons';
import { Position } from '../../../app/interface/position.interface';
import { environment } from '../../../environments/environment';
import { PositionDeleteModalComponent } from '../../modal/position/position-delete-modal/position-delete-modal.component';

@Component({
  selector: 'app-department-table',
  imports: [CommonModule, AvatarComponent, ButtonDirective, ModalToggleDirective, IconDirective, SmartTableComponent, TemplateIdDirective, PositionDeleteModalComponent],
  templateUrl: './department-table.component.html',
  styleUrl: './department-table.component.scss',
})
export class DepartmentTableComponent implements OnChanges {
  @Input() data: Position[] = [];
  @Input() resetKey = 0;
  @Input() noMargin = true;
  @Input() hideDeactiveButton = false;
  @Output() updatePosition = new EventEmitter<number>();
  @Output() deactivePosition = new EventEmitter<number>();

  protected readonly apiUrl = environment.apiUrl;
  protected readonly icons = { cilX };
  protected loading = true;
  protected tableFilterValue = '';
  protected columns: IColumn[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['resetKey'] && !changes['resetKey'].firstChange) {
      this.tableFilterValue = '';
    }

    this.columns = [
      this.column('id', '#'),
      this.column('name', 'Nome'),
      this.column('manangers', 'Gestores', false),
      this.column('updatedAt', 'Modificado'),
      this.column('createdAt', 'Criado'),
      ...(!this.hideDeactiveButton ? [this.column('actions', '', false)] : []),
    ];
    this.loading = true;
    queueMicrotask(() => this.loading = false);
  }

  protected openDepartment(event: { item: Position }): void {
    this.updatePosition.emit(event.item.id);
  }

  protected deactivatePosition(id: number): void {
    this.deactivePosition.emit(id);
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
