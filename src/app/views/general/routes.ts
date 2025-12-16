import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Geral'
    },
    children: [
      {
        path: '',
        redirectTo: 'internal-control',
        pathMatch: 'full'
      },
      {
        path: 'internal-control',
        loadComponent: () => import('./internal-control/internal-control.component').then(m => m.InternalControlComponent),
        data: {
          title: 'Controle Interno'
        }
      },
      {
        path: 'raw-materials',
        loadComponent: () => import('./raw-materials/raw-materials.component').then(m => m.RawMaterialsComponent),
        data: {
          title: 'Matérias primas'
        }
      },
      {
        path: 'todo',
        loadComponent: () => import('./todo/todo.component').then(m => m.TodoComponent),
        data: {
          title: 'Para fazer'
        }
      },
    ]
  }
];
