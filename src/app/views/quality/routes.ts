import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Qualidade'
    },
    children: [
      {
        path: '',
        redirectTo: 'checklist',
        pathMatch: 'full'
      },
      {
        path: 'checklist',
        loadComponent: () => import('./checklist/checklist.component').then(m => m.ChecklistComponent),
        data: {
          title: 'Checklist'
        }
      },
    ]
  }
];