import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./events.component').then(m => m.EventsComponent),
    data: {
      title: 'Eventos'
    }
  }
];