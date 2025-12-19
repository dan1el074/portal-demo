import { Routes } from '@angular/router';
import { AuthGuard } from 'src/app/config/authGuard';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Gestão'
    },
    children: [
      {
        path: '',
        redirectTo: 'users',
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
    ]
  }
];
