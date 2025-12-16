import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: '',
    loadComponent: () => import('./layout/default-layout').then((m) => m.DefaultLayoutComponent), data: { title: 'Home' },
    children: [
      {
        path: 'home',
        loadChildren: () => import('./views/home/routes').then((m) => m.routes),
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./views/dashboard/routes').then((m) => m.routes),
      },
      {
        path: 'newspaper',
        loadChildren: () => import('./views/newspaper/routes').then((m) => m.routes),
      },
      {
        path: 'events',
        loadChildren: () => import('./views/events/routes').then((m) => m.routes),
      },
      {
        path: 'config',
        loadChildren: () => import('./views/config/routes').then((m) => m.routes),
      },
      {
        path: 'administration',
        loadChildren: () => import('./views/administration/routes').then((m) => m.routes),
      },
      {
        path: 'general',
        loadChildren: () => import('./views/general/routes').then((m) => m.routes),
      },
      {
        path: 'quality',
        loadChildren: () => import('./views/quality/routes').then((m) => m.routes),
      },
    ],
  },
  {
    path: '404',
    loadComponent: () => import('./views/errors/page404/page404.component')
      .then((m) => m.Page404Component), data: { title: 'Page 404' },
  },
  {
    path: '500',
    loadComponent: () => import('./views/errors/page500/page500.component')
      .then((m) => m.Page500Component), data: { title: 'Page 500' },
  },
  {
    path: 'login',
    loadComponent: () => import('./views/login/login.component')
      .then((m) => m.LoginComponent), data: { title: 'Login Page' },
  },
  {
    path: '**',
    redirectTo: '404'
  },
];