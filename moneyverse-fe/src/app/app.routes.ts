import {Routes} from '@angular/router';
import {AppComponent} from './app.component';
import {canActivateAuthRole} from './core/auth/auth.guard';
import {OnboardingComponent} from './features/user-management/pages/onboarding/onboarding/onboarding.component';

export const routes: Routes = [
  {
    path: '',
    component: AppComponent,
    canActivate: [canActivateAuthRole]
  },
  {
    path: 'onboarding',
    component: OnboardingComponent,
    canActivate: [canActivateAuthRole],
  }
];
