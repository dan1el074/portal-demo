import { AuthGuard } from './../../config/authGuard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Apps'
    },
    children: [
      {
        path: '',
        redirectTo: '/',
        pathMatch: 'full'
      },
      {
        path: 'mes',
        loadComponent: () => import('./mes/mes.component').then(m => m.MesComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_MES'],
          title: 'MES'
        }
      },
    ]
  }
];
