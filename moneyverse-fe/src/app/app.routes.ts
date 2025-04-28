import {Routes} from '@angular/router';
import {canActivateAuthRole, canUseApplication} from './core/auth/auth.guard';
import {OnboardingComponent} from './features/onboarding/onboarding.component';
import {OverviewComponent} from './features/overview/overview.component';
import {MainLayoutComponent} from './core/layout/main-layout/main-layout.component';
import {AccountComponent} from './features/account-management/account/account.component';
import {CategoryComponent} from './features/budget-management/category/category.component';

export const routes: Routes = [
    {
      path: '',
      component: MainLayoutComponent,
      canActivate: [canActivateAuthRole, canUseApplication],
      data: {role: 'view-profile'},
      children: [
        {
          path: '',
          redirectTo: 'overview',
          pathMatch: 'full'
        },
        {
          path: 'overview',
          component: OverviewComponent
        },
        {
          path: 'settings',
          loadChildren: () =>
            import('./features/settings/settings.module').then(m => m.SettingsModule)
        },
        {
          path: 'accounts',
          component: AccountComponent
        },
        {
          path: 'categories',
          component: CategoryComponent
        }
      ]
    },
    {
      path: 'onboarding',
      component: OnboardingComponent,
      canActivate: [canActivateAuthRole],
      data: {role: 'view-profile'}

    },
    {
      path: '**',
      redirectTo: 'overview'
    }
  ]
;
