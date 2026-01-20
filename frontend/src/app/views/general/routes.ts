import { Routes } from '@angular/router';
import { AuthGuard } from '../../config/authGuard';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Geral'
    },
    children: [
      {
        path: '',
        redirectTo: '/',
        pathMatch: 'full'
      },
      {
        path: 'internal-control',
        loadComponent: () => import('./internal-control/internal-control.component').then(m => m.InternalControlComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_INTERNAL_CONTROL'],
          title: 'Controle Interno'
        }
      },
      {
        path: 'raw-materials',
        loadComponent: () => import('./raw-materials/raw-materials.component').then(m => m.RawMaterialsComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_RAW_MATERIALS'],
          title: 'Matérias primas'
        }
      },
      {
        path: 'todo',
        loadComponent: () => import('./todo/todo.component').then(m => m.TodoComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_TODO'],
          title: 'Para fazer'
        }
      },
    ]
  }
];
