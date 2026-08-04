import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { AvatarComponent, ButtonDirective, ModalToggleDirective } from '@coreui/angular';
import { IColumn, SmartTableComponent, TemplateIdDirective } from '@coreui/angular-pro';
import { IconDirective } from '@coreui/icons-angular';
import { cilX } from '@coreui/icons';
import { UserTable } from '../../../app/interface/user.interface';
import { UserDeleteModalComponent } from '../../modal/user/user-delete-modal/user-delete-modal.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-user-table',
  imports: [CommonModule, AvatarComponent, ButtonDirective, ModalToggleDirective, IconDirective, SmartTableComponent, TemplateIdDirective, UserDeleteModalComponent],
  templateUrl: './user-table.component.html',
  styleUrl: './user-table.component.scss',
})
export class UserTableComponent implements OnChanges {
  @Input() data: UserTable[] = [];
  @Input() resetKey = 0;
  @Input() noMargin = false;
  @Input() hideDeactiveButton = false;
  @Output() updateUser = new EventEmitter<number>();
  @Output() deactivateTask = new EventEmitter<number>();

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
      this.column('name', 'Nome'),
      this.column('username', 'Usuário'),
      this.column('position', 'Cargo'),
      this.column('email', 'E-mail'),
      this.column('updateAt', 'Modificado'),
      ...(!this.hideDeactiveButton ? [this.column('actions', '', false)] : []),
    ];
    this.loading = true;
    queueMicrotask(() => this.loading = false);
  }

  protected openUser(event: { item: UserTable }): void {
    this.updateUser.emit(event.item.id);
  }

  protected deactivateUser(id: number): void {
    this.deactivateTask.emit(id);
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
