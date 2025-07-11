import {ApplicationConfig, importProvidersFrom, inject, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideKeycloakAngular} from './core/auth/auth.config';
import {
  HttpClient,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  provideHttpClient,
  withInterceptors
} from '@angular/common/http';
import {includeBearerTokenInterceptor} from 'keycloak-angular';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {provideEchartsCore} from 'ngx-echarts';
import * as echarts from 'echarts/core';
import {SVGRenderer} from 'echarts/renderers';
import {GridComponent, LegendComponent, TitleComponent, TooltipComponent} from 'echarts/components';
import {BarChart, LineChart, PieChart} from 'echarts/charts';
import {provideAngularSvgIcon} from 'angular-svg-icon';
import {LoadingService} from './shared/services/loading.service';
import {finalize} from 'rxjs';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {providePrimeNG} from 'primeng/config';
import {MoneyversePreset} from '../assets/themes/moneyverse-preset';

echarts.use([TitleComponent, TooltipComponent, LegendComponent, GridComponent, PieChart, BarChart, LineChart, SVGRenderer]);

const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http);

const loadingInterceptor: HttpInterceptorFn =
  (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
    const loadingService = inject(LoadingService);
    loadingService.loadingOn();
    return next(req)
      .pipe(
        finalize(() => {
          loadingService.loadingOff()
        })
      )
  }

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloakAngular(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptors([
      includeBearerTokenInterceptor,
      loadingInterceptor
    ])),
    importProvidersFrom([TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    })]),
    provideEchartsCore({echarts}),
    provideAngularSvgIcon(),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: MoneyversePreset,
        options: {
          prefix: 'p',
          darkModeSelector: '.p-dark',
          cssLayer: {
            name: 'primeng',
            order: 'theme, base, primeng'
          }
        }
      }
    })
  ]
};
