import {Routes} from '@angular/router';
import {canActivateAuthRole, canUseApplication} from './core/auth/auth.guard';
import {OnboardingComponent} from './features/onboarding/onboarding.component';
import {OverviewComponent} from './features/overview/overview.component';
import {LayoutComponent} from './core/layout/components/layout.component';

export const routes: Routes = [
    {
      path: '',
      component: LayoutComponent,
      canActivate: [canActivateAuthRole],
      data: {role: 'view-profile'},
      children: [
        {
          path: '',
          redirectTo: 'overview',
          pathMatch: 'full'
        },
        {
          path: 'onboarding',
          component: OnboardingComponent
        },
        {
          path: 'overview',
          component: OverviewComponent,
          canActivate: [canUseApplication],
        },
        {
          path: 'settings',
          canActivate: [canUseApplication],
          loadChildren: () =>
            import('./features/settings/settings.route')
        },
        {
          path: 'accounts',
          canActivate: [canUseApplication],
          loadChildren: () =>
            import('./features/account/account.routes')
        },
        {
          path: 'categories',
          canActivate: [canUseApplication],
          loadChildren: () =>
            import('./features/category/category.routes')
        },
        {
          path: 'transactions',
          canActivate: [canUseApplication],
          loadChildren: () =>
            import('./features/transaction/transaction.routes')
        }
      ]
    },
    {
      path: '**',
      redirectTo: 'overview'
    }
  ]
;
