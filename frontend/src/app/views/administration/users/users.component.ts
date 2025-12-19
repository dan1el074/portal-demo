import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { UserService } from './../../../services/user.service';
import { UserTable } from '../../../interface/user.interface';
import { UserTableComponent } from '../../../../components/table/user-table/user-table.component';
import { Router } from '@angular/router';
import { IconDirective } from '@coreui/icons-angular';
import { cilPlus } from '@coreui/icons';

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
    UserTableComponent
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected activeUsers: Array<UserTable> = [];
  protected inactiveUsers: Array<UserTable> = [];
  protected icons = { cilPlus };

  constructor(
    private router: Router,
    private userService: UserService,
    private toasterService: ToastrService
  ) {}

  ngOnInit(): void {
    this.userService.findAll().subscribe({
      next: userList => {
        this.updateUsers(userList);
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

  private updateUsers(newUsers: Array<UserTable>) {
    this.activeUsers = newUsers.filter(user => user.activated)
    this.inactiveUsers = newUsers.filter(user => !user.activated)
  }
}
