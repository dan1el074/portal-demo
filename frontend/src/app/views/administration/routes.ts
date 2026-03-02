import { AuthGuard } from './../../config/authGuard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Gestão'
    },
    children: [
      {
        path: '',
        redirectTo: '/',
        pathMatch: 'full'
      },
      {
        path: 'users',
        loadComponent: () => import('./users/users.component').then(m => m.UsersComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_ADM_PANEL'],
          title: 'Usuários'
        }
      },
      {
        path: 'departments',
        loadComponent: () => import('./departments/departments.component').then(m => m.DepartmentsComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_ADM_PANEL'],
          title: 'Usuários'
        }
      },
    ]
  }
];
