import { Routes } from '@angular/router';
import { AuthGuard } from '../../config/authGuard';
import { DocumentViewComponent } from './internal-communication/document-view/document-view.component';

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
        path: 'internal-communication',
        loadComponent: () => import('./internal-communication/internal-communication.component').then(m => m.InternalCommunicationComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_INTERNAL_COMMUNICATION'],
          title: 'Comunicação Interna'
        }
      },
      {
        path: 'internal-communication/:id',
        loadComponent: () => import('./internal-communication/document-view/document-view.component').then(m => m.DocumentViewComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_INTERNAL_COMMUNICATION'],
          title: 'Comunicação Interna'
        }
      },
      {
        path: 'internal-communication/:id/edit',
        loadComponent: () => import('./internal-communication/document-view/edit-ci/edit-ci.component').then(m => m.EditCiComponent),
        canActivate: [AuthGuard],
        data: {
          roles: ['ROLE_ADMIN','ROLE_INTERNAL_COMMUNICATION'],
          title: 'Comunicação Interna'
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
