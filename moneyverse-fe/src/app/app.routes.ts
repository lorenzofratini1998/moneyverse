import {Routes} from '@angular/router';
import {AppComponent} from './app.component';
import {canActivateAuthRole} from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: AppComponent,
    canActivate: [canActivateAuthRole]
  }
];
