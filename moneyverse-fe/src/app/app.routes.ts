import {Routes} from '@angular/router';
import {canActivateAuthRole, canUseApplication} from './core/auth/auth.guard';
import {OnboardingComponent} from './features/onboarding/onboarding.component';
import {OverviewComponent} from './features/overview/overview.component';
import {MainLayoutComponent} from './core/layout/main-layout/main-layout.component';
import {TransactionComponent} from './features/transaction-management/transaction/transaction.component';

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
          loadChildren: () =>
            import('./features/account/account.module').then(m => m.AccountModule)
        },
        {
          path: 'categories',
          loadChildren: () =>
            import('./features/category/category.module').then(m => m.CategoryModule)
        },
        {
          path: 'transactions',
          component: TransactionComponent
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
