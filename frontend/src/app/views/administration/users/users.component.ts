import { UserData } from './../../../interface/user.interface';
import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { UserService } from './../../../services/user.service';
import { UserTable } from '../../../interface/user.interface';
import { UserTableComponent } from '../../../../components/table/user-table/user-table.component';
import { Router } from '@angular/router';
import { IconDirective } from '@coreui/icons-angular';
import { cilPlus, cilX } from '@coreui/icons';
import { UserFormComponent } from '../../../../components/forms/user-form/user-form.component';
import { ChangeDetectorRef } from '@angular/core';
import { UserEditFormComponent } from '../../../../components/forms/user-edit-form/user-edit-form.component';
import { PostitionService } from '../../../services/position.service';
import { Position } from '../../../interface/position.interface';

@Component({
  selector: 'app-users',
  imports: [
    IconDirective,
    RowComponent,
    ButtonDirective,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    Tabs2Module,
    UserTableComponent,
    UserFormComponent,
    UserEditFormComponent
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected activeUsers: Array<UserTable> = [];
  protected inactiveUsers: Array<UserTable> = [];
  protected positions!: Array<Position>;
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected newUserTab = false;
  protected editUserTab = false;
  protected editUserData: UserData = {
    id: 0,
    pictureId: null,
    name: '',
    positionId: 1,
    email: '',
    birthDate: '',
    username: '',
    roles: [],
    activated: false
  };

  constructor(
    private router: Router,
    private userService: UserService,
    private postitionService: PostitionService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnInit(): void {
    this.userService.findAll().subscribe({
      next: userList => {
        this.updateUsers(userList);
        this.loadPositions();
      },
      error: error => {
        if (error.status == 401) {
          this.router.navigate(['login']);
          this.toasterService.error('Sessão expirada!');
        } else{
          this.toasterService.error('Erro ao carregar usuários!');
        }
      }
    });
  }

  private loadPositions(): void {
    this.postitionService.findAll().subscribe({
      next: positionsList => {
        this.positions = positionsList;
        this.cdr.detectChanges();
      },
      error: error => this.toasterService.error('Erro ao carregar nome de setores!')
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
    this.activeItemKey = parsedKey;
  }

  protected toggleNewUserTab(status = !this.newUserTab): void {
    this.newUserTab = status;
    status ? this.activeItemKey = 2 : this.activeItemKey = 0;
  }

  protected onCreateUser(data: FormData): void {
    this.userService.insert(data).subscribe({
      next: (data: Array<UserTable>) => {
        this.updateUsers(data);
        this.toasterService.success('Usuário criado com sucesso!');
      },
      error: () => this.toasterService.error('Erro ao criar usuário!')
    });
  }

  protected toggleEditUserTab(status: boolean): void {
    this.editUserTab = status;
    status ? this.activeItemKey = 3 : this.activeItemKey = 0;
  }

  protected updateUserTask(id: number): void {
    this.userService.findById(id).subscribe({
      next: (data: UserData) => {
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
      },
      error: () => this.toasterService.error('Erro ao editar usuário!'),
      complete: () => this.toggleEditUserTab(false),
    });
  }

  protected deactivateUser(id: number): void {
    this.userService.deactivateUser(id).subscribe({
      next: (data: Array<UserTable>) => {
        this.updateUsers(data);
        this.toasterService.success("Usuário editado com sucesso!");
      },
      error: (e: any) => {
        this.toasterService.error('Erro ao editar usuário!', e);
      }
    })
  }
}
