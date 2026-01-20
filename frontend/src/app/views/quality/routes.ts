import { Routes } from '@angular/router';
import { AuthGuard } from '../../config/authGuard';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Qualidade'
    },
    children: [
      {
        path: '',
        redirectTo: '/',
        pathMatch: 'full'
      },
      {
        path: 'checklist',
        loadComponent: () => import('./checklist/checklist.component').then(m => m.ChecklistComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_CHECKLIST'],
          title: 'Checklist'
        }
      },
    ]
  }
];
