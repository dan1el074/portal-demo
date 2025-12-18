import { ApplicationConfig } from '@angular/core';
import { provideRouter, withEnabledBlockingInitialNavigation, withHashLocation, withInMemoryScrolling, withRouterConfig, withViewTransitions } from '@angular/router';
import { IconSetService } from '@coreui/icons-angular';
import { routes } from './app.routes';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { CustomPaginator } from './shared/CustomPaginator';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';

export const appConfig: ApplicationConfig = {
  providers: [
    IconSetService,
    provideRouter(
      routes,
      withRouterConfig({ onSameUrlNavigation: 'reload' }),
      withInMemoryScrolling({ scrollPositionRestoration: 'top', anchorScrolling: 'enabled' }),
      withEnabledBlockingInitialNavigation(),
      withViewTransitions(),
      withHashLocation()
    ),
    { provide: MatPaginatorIntl, useFactory: CustomPaginator },
    provideAnimations(),
    provideToastr({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      closeButton: true,
      progressBar: true,
    }),
  ]
};

