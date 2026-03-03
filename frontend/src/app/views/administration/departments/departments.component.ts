import { Component } from '@angular/core';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { cilPlus } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { DepartmentTableComponent } from '../../../../components/table/department-table/department-table.component';
import { Position, PositionTable } from '../../../interface/position.interface';

@Component({
  selector: 'app-departments',
  imports: [
    ColComponent,
    RowComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    ButtonDirective,
    IconDirective,
    Tabs2Module,
    DepartmentTableComponent
  ],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.scss',
})
export class DepartmentsComponent {
  protected activePositions: Array<PositionTable> = [
    {
      id: 1,
      name: "TI",
      managers: [
        {
          id: 1,
          name: "Daniel",
          position: {
            id: 1,
            name: "TI"
          },
          picture: {
            id: 1
          }
        },
        {
          id: 2,
          name: "Carlos",
          position: {
            id: 2,
            name: "Compras"
          },
          picture: {
            id: 2
          }
        },
      ],
      updatedAt: "2026-02-04T17:10:56Z",
      createdAt: "2026-02-04T17:10:56Z"
    },
    {
      id: 2,
      name: "Compras",
      managers: null,
      updatedAt: "2026-02-04T17:10:56Z",
      createdAt: "2026-02-04T17:10:56Z"
    },
    {
      id: 4,
      name: "Corte/Dobra",
      managers: [
        {
          id: 4,
          name: "Juliano",
          position: {
            id: 1,
            name: "Processos"
          },
          picture: {
            id: 3
          }
        }
      ],
      updatedAt: "2026-02-04T17:10:56Z",
      createdAt: "2026-02-04T17:10:56Z"
    }
  ];
  protected inactivePositions: Array<PositionTable> = [
    {
      id: 2,
      name: "Processos",
      managers: [
        {
          id: 4,
          name: "Julia",
          position: {
            id: 1,
            name: "Processos"
          },
          picture: null
        }
      ],
      updatedAt: "2026-02-04T17:10:56Z",
      createdAt: "2026-02-04T17:10:56Z"
    }
  ];
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected icons = { cilPlus };
  protected activeItemKey = 0;
  protected newUserTab = false;
  protected editUserTab = false;
  protected positionUserData: Position = {
    id: 0,
    name: ""
  };

  // public ngOnInit(): void {
  //   this.userService.findAll().subscribe({
  //     next: userList => {
  //       this.updateUsers(userList);
  //       this.loadPositions();
  //     },
  //     error: error => {
  //       if (error.status == 401) {
  //         this.router.navigate(['login']);
  //         this.toasterService.error('Sessão expirada!');
  //       } else{
  //         this.toasterService.error('Erro ao carregar usuários!');
  //       }
  //     }
  //   });
  // }

  // private loadPositions(): void {
  //   this.postitionService.findAll().subscribe({
  //     next: positionsList => {
  //       this.positions = positionsList;
  //       this.cdr.detectChanges();
  //     },
  //     error: error => this.toasterService.error('Erro ao carregar nome de setores!')
  //   });
  // }

  // private updateUsers(newUsers: Array<UserTable>): void {
  //   this.activeUsers = newUsers.filter(user => user.activated);
  //   this.inactiveUsers = newUsers.filter(user => !user.activated);
  //   this.cdr.detectChanges();
  // }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;
    this.activeItemKey = parsedKey;
  }

  // protected toggleNewUserTab(status = !this.newUserTab): void {
  //   this.newUserTab = status;
  //   status ? this.activeItemKey = 2 : this.activeItemKey = 0;
  // }

  // protected onCreateUser(data: FormData): void {
  //   this.userService.insert(data).subscribe({
  //     next: (data: Array<UserTable>) => {
  //       this.updateUsers(data);
  //       this.toasterService.success('Usuário criado com sucesso!');
  //       this.toggleNewUserTab(false)
  //     },
  //     error: () => this.toasterService.error('Erro ao criar usuário!')
  //   });
  // }

  // protected toggleEditUserTab(status: boolean): void {
  //   this.editUserTab = status;
  //   status ? this.activeItemKey = 3 : this.activeItemKey = 0;
  // }

  // protected updateUserTask(id: number): void {
  //   this.userService.findById(id).subscribe({
  //     next: (data: UserData) => {
  //       this.editUserData = data;
  //       this.toggleEditUserTab(true);
  //       this.cdr.detectChanges();
  //     },
  //     error: () => {
  //       this.toasterService.error('Erro ao buscar informções do usuário');
  //       this.toggleEditUserTab(false);
  //     }
  //   });
  // }

  // protected clearUserEdit(): void {
  //   this.editUserData = {
  //     id: 0,
  //     pictureId: null,
  //     name: '',
  //     positionId: 1,
  //     email: '',
  //     birthDate: '',
  //     username: '',
  //     supportToken: null,
  //     roles: [],
  //     activated: false
  //   };
  // }

  // protected onUpdateUser(form: {data: FormData, id: number}): void {
  //   if (!form.id) {
  //     this.toasterService.error('Erro ao editar usuário!');
  //     return;
  //   };

  //   this.userService.update(form.id, form.data).subscribe({
  //     next: (data: Array<UserTable>) => {
  //       this.updateUsers(data);
  //       this.cdr.detectChanges();
  //       this.toasterService.success('Usuário editado com sucesso!');
  //       this.toggleEditUserTab(false)
  //     },
  //     error: () => this.toasterService.error('Erro ao editar usuário!')
  //   });
  // }

  // protected deactivateUser(id: number): void {
  //   this.userService.deactivateUser(id).subscribe({
  //     next: (data: Array<UserTable>) => {
  //       this.updateUsers(data);
  //       this.toasterService.success("Usuário editado com sucesso!");
  //     },
  //     error: (e: any) => {
  //       this.toasterService.error('Erro ao editar usuário!', e);
  //     }
  //   })
  // }

}
