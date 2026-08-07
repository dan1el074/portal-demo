import { ApplicationConfig } from '@angular/core';
import { provideRouter, withHashLocation, withRouterConfig } from '@angular/router';
import { routes } from './app.routes';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { CustomPaginator } from './shared/CustomPaginator';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors, withXhr } from '@angular/common/http';
import { authInterceptor } from './interceptor/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withRouterConfig({ onSameUrlNavigation: 'reload' }),
      withHashLocation()
    ),
    { provide: MatPaginatorIntl, useFactory: CustomPaginator },
    provideAnimations(),
    provideHttpClient(withXhr(),
      withInterceptors([authInterceptor])
    )
  ]
};
