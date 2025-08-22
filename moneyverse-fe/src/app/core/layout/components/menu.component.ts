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
      label: 'menu.overview'
    },
    {
      routerLink: '/accounts',
      icon: 'layers',
      label: 'menu.accounts.title',
      items: [
        {
          routerLink: '/accounts/dashboard',
          icon: 'chart-pie',
          label: 'menu.accounts.dashboard'
        },
        {
          routerLink: '/accounts/manage',
          icon: 'table',
          label: 'menu.accounts.manage'
        }
      ]
    },
    {
      routerLink: '/categories',
      icon: 'shapes',
      label: 'menu.categories.title',
      items: [
        {
          routerLink: '/categories/dashboard',
          icon: 'chart-pie',
          label: 'menu.categories.dashboard'
        },
        {
          routerLink: '/categories/budgeting',
          icon: 'coins',
          label: 'menu.categories.budgeting'
        },
        {
          routerLink: '/categories/manage',
          icon: 'table',
          label: 'menu.categories.manage'
        }
      ]
    },
    {
      routerLink: '/transactions',
      icon: 'credit-card',
      label: 'menu.transactions.title',
      items: [
        {
          routerLink: '/transactions/analytics',
          icon: 'chart-pie',
          label: 'menu.transactions.analytics'
        },
        {
          routerLink: '/transactions/manage',
          icon: 'table',
          label: 'menu.transactions.manage'
        },
        {
          routerLink: '/transactions/tags',
          icon: 'tag',
          label: 'menu.transactions.tags'
        },
        {
          routerLink: '/transactions/subscriptions',
          icon: 'calendar-sync',
          label: 'menu.transactions.subscriptions'
        }
      ]
    }
  ];
}
