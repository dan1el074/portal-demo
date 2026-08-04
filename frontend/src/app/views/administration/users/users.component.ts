import { Component, OnInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { ButtonDirective, CardBodyComponent, CardComponent, ContainerComponent, Tabs2Module } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilPlus, cilX } from '@coreui/icons';
import { ToastrService } from 'ngx-toastr';
import { UserFormComponent } from '../../../../components/forms/user/user-form/user-form.component';
import { UserEditFormComponent } from '../../../../components/forms/user/user-edit-form/user-edit-form.component';
import { UserTableComponent } from '../../../../components/table/user-table/user-table.component';
import { ErrorService } from './../../../services/error.service';
import { UserService } from './../../../services/user.service';
import { PostitionService } from '../../../services/position.service';
import { RoleService } from './../../../services/role.service';
import { PositionMin } from '../../../interface/position.interface';
import { RoleGroup } from './../../../interface/role.interface';
import { UserEditData } from './../../../interface/user.interface';
import { UserTable } from '../../../interface/user.interface';
import { BackNavigationService } from '../../../services/back-navigation.service';

@Component({
  selector: 'app-users',
  imports: [
    ContainerComponent,
    IconDirective,
    ButtonDirective,
    CardComponent,
    CardBodyComponent,
    Tabs2Module,
    UserTableComponent,
    UserFormComponent,
    UserEditFormComponent
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent implements OnInit {
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected activeUsers: Array<UserTable> = [];
  protected inactiveUsers: Array<UserTable> = [];
  protected positions: Array<PositionMin> = [];
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected allRoles: Array<RoleGroup> = [];
  protected newUserTab = false;
  protected editUserTab = false;
  protected tableResetKey = 0;
  private formHistoryActive = false;
  private historyCloseTargetTab = 0;
  protected editUserData: UserEditData = {
    id: 0,
    pictureId: null,
    name: '',
    positionId: 1,
    email: '',
    birthDate: '',
    username: '',
    supportToken: null,
    roles: [],
    activated: false
  };

  constructor(
    private userService: UserService,
    private postitionService: PostitionService,
    private roleService: RoleService,
    private errorService: ErrorService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef,
    private backNav: BackNavigationService
  ) {}

  public ngOnInit(): void {
    this.userService.findAll().subscribe({
      next: userList => {
        this.updateUsers(userList);
        this.loadPositions();
      },
      error: () => this.toasterService.error('Erro ao carregar usuários!')
    });

    this.roleService.findAll().subscribe({
      next: (roles: Array<RoleGroup>) => this.allRoles = roles,
      error: () => this.toasterService.error("Erro ao carregar acessos!")
    });
  }

  private loadPositions(): void {
    this.postitionService.list().subscribe({
      next: positionsList => {
        this.positions = positionsList;
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao carregar nome de setores!')
    });
  }

  private updateUsers(newUsers: Array<UserTable>): void {
    this.activeUsers = newUsers.filter(user => user.activated);
    this.inactiveUsers = newUsers.filter(user => !user.activated);
    this.cdr.detectChanges();
  }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;

    if (parsedKey <= 1 && (this.newUserTab || this.editUserTab)) {
      this.closeFormTabs(parsedKey);
      return;
    }

    this.activeItemKey = parsedKey;
  }

  protected resetTableFilters(): void {
    if (this.newUserTab || this.editUserTab) {
      this.closeFormTabs(0);
    } else {
      this.activeItemKey = 0;
    }
    this.tableResetKey++;
  }

  protected toggleNewUserTab(status = !this.newUserTab): void {
    if (status) {
      this.editUserTab = false;
      this.newUserTab = true;
      this.activeItemKey = 2;
      this.registerFormHistory();
      return;
    }

    this.closeFormTabs();
  }

  protected onCreateUser(data: FormData): void {
    this.userService.insert(data).subscribe({
      next: (data: Array<UserTable>) => {
        this.updateUsers(data);
        this.toasterService.success('Usuário criado com sucesso!');
        this.toggleNewUserTab(false)
      },
      error: (error) => this.errorService.showError(error)
    });
  }

  protected toggleEditUserTab(status: boolean): void {
    if (status) {
      this.newUserTab = false;
      this.editUserTab = true;
      this.activeItemKey = 3;
      this.registerFormHistory();
      return;
    }

    this.closeFormTabs();
  }

  private registerFormHistory(): void {
    if (this.formHistoryActive) return;

    this.formHistoryActive = true;
    this.historyCloseTargetTab = 0;
    this.backNav.register(() => {
      const targetTab = this.historyCloseTargetTab;
      this.formHistoryActive = false;
      this.historyCloseTargetTab = 0;
      this.resetFormTabs(targetTab);
    });
  }

  private closeFormTabs(targetTab = 0): void {
    const shouldRemoveHistory = this.formHistoryActive;
    this.formHistoryActive = false;
    this.historyCloseTargetTab = targetTab;
    this.resetFormTabs(targetTab);

    if (shouldRemoveHistory) {
      this.backNav.unregister();
    } else {
      this.historyCloseTargetTab = 0;
    }
  }

  private resetFormTabs(targetTab = 0): void {
    this.newUserTab = false;
    this.editUserTab = false;
    this.activeItemKey = targetTab;
    this.clearUserEdit();
    this.cdr.detectChanges();
  }

  protected updateUserTask(id: number): void {
    this.userService.findById(id).subscribe({
      next: (data: UserEditData) => {
        this.editUserData = data;
        this.toggleEditUserTab(true);
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao buscar informções do usuário');
        this.toggleEditUserTab(false);
      }
    });
  }

  protected clearUserEdit(): void {
    this.editUserData = {
      id: 0,
      pictureId: null,
      name: '',
      positionId: 1,
      email: '',
      birthDate: '',
      username: '',
      supportToken: null,
      roles: [],
      activated: false
    };
  }

  protected onUpdateUser(form: {data: FormData, id: number}): void {
    if (!form.id) {
      this.toasterService.error('Erro ao editar usuário!');
      return;
    };

    this.userService.update(form.id, form.data).subscribe({
      next: (data: Array<UserTable>) => {
        this.updateUsers(data);
        this.cdr.detectChanges();
        this.toasterService.success('Usuário editado com sucesso!');
        this.toggleEditUserTab(false)

        if (form.id == this.userService.getCurrentUser()?.id) {
          this.userService.refreshUser().subscribe();
        }
      },
      error: (error) => this.errorService.showError(error)
    });
  }

  protected deactivateUser(id: number): void {
    this.userService.deactivateUser(id).subscribe({
      next: (data: Array<UserTable>) => {
        this.updateUsers(data);
        this.toasterService.success("Usuário editado com sucesso!");
      },
      error: (error) => this.errorService.showError(error)
    })
  }
}
