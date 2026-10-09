import { Routes } from '@angular/router';
import { AuthGuard } from '../../config/authGuard';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'PCP'
    },
    children: [
      {
        path: '',
        redirectTo: '/',
        pathMatch: 'full'
      },
      {
        path: 'kanbam-exclusion',
        loadComponent: () => import('./kanbam-exclusion/kanbam-exclusion.component')
          .then(component => component.KanbamExclusionComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN', 'ROLE_KANBAM_EXCLUSION'],
          title: 'Exclusão de Kanbam'
        }
      }
    ]
  }
];
