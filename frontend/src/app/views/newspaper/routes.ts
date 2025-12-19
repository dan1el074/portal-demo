import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./newspaper.component').then(m => m.NewspaperComponent),
    data: {
      title: 'Jornal Digital'
    }
  }
];