import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideKeycloakAngular} from './core/auth/auth.config';
import {HttpClient, provideHttpClient, withInterceptors} from '@angular/common/http';
import {includeBearerTokenInterceptor} from 'keycloak-angular';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {provideEchartsCore} from 'ngx-echarts';
import * as echarts from 'echarts/core';
import {SVGRenderer} from 'echarts/renderers';
import {LegendComponent, TitleComponent, TooltipComponent} from 'echarts/components';
import {PieChart} from 'echarts/charts';
import {provideAngularSvgIcon} from 'angular-svg-icon';

echarts.use([TitleComponent, TooltipComponent, LegendComponent, PieChart, SVGRenderer]);

const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http);

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloakAngular(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptors([
      includeBearerTokenInterceptor
    ])),
    importProvidersFrom([TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    })]),
    provideEchartsCore({echarts}),
    provideAngularSvgIcon()
  ]
};
