import {Component} from '@angular/core';
import {LucideAngularModule} from 'lucide-angular';
import {MenuItem} from 'primeng/api';
import {MenuItemComponent} from './menu-item/menu-item.component';

@Component({
  selector: 'app-menu',
  imports: [
    LucideAngularModule,
    MenuItemComponent
  ],
  template: `
    <ul class="layout-menu">
      @for (item of model; track $index) {
        <app-menu-item [item]="item"></app-menu-item>
      }
    </ul>
  `
})
export class MenuComponent {

  model: MenuItem[] = [
    {
      routerLink: '/overview',
      icon: 'home',
      label: 'app.overview'
    },
    {
      routerLink: '/accounts',
      icon: 'layers',
      label: 'app.accounts',
      items: [
        {
          routerLink: '/accounts/analytics',
          icon: 'chart-pie',
          label: 'app.analytics'
        },
        {
          routerLink: '/accounts/manage',
          icon: 'table',
          label: 'app.manageAccounts'
        }
      ]
    },
    {
      routerLink: '/categories',
      icon: 'shapes',
      label: 'app.categories',
      items: [
        {
          routerLink: '/categories/analytics',
          icon: 'chart-pie',
          label: 'app.analytics'
        },
        {
          routerLink: '/categories/budgeting',
          icon: 'coins',
          label: 'app.budgeting'
        },
        {
          routerLink: '/categories/manage',
          icon: 'table',
          label: 'app.manageCategories'
        }
      ]
    },
    {
      routerLink: '/transactions',
      icon: 'credit-card',
      label: 'app.transactions',
      items: [
        {
          routerLink: '/transactions/analytics',
          icon: 'chart-pie',
          label: 'app.analytics'
        },
        {
          routerLink: '/transactions/manage',
          icon: 'table',
          label: 'app.manageTransactions'
        },
        {
          routerLink: '/transactions/tags',
          icon: 'tag',
          label: 'app.tags'
        },
        {
          routerLink: '/transactions/subscriptions',
          icon: 'calendar-sync',
          label: 'app.subscriptions'
        }
      ]
    }
  ];
}
