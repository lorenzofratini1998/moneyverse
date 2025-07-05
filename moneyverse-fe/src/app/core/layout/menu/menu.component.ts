import {Component} from '@angular/core';
import {RouterLink} from '@angular/router';
import {LucideAngularModule} from 'lucide-angular';
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {MenuListComponent} from './components/menu-list/menu-list.component';
import {MenuItem} from './menu.model';

@Component({
  selector: 'app-menu',
  imports: [
    RouterLink,
    LucideAngularModule,
    SvgComponent,
    MenuListComponent
  ],
  templateUrl: './menu.component.html'
})
export class MenuComponent {

  menuItems: MenuItem[] = [
    {
      path: '/overview',
      icon: 'home',
      translationKey: 'menu.overview'
    },
    {
      path: '/accounts',
      icon: 'layers',
      translationKey: 'menu.accounts.title',
      children: [
        {
          path: '/accounts/dashboard',
          icon: 'chart-pie',
          translationKey: 'menu.accounts.dashboard'
        },
        {
          path: '/accounts/manage',
          icon: 'table',
          translationKey: 'menu.accounts.manage'
        }
      ]
    },
    {
      path: '/categories',
      icon: 'shapes',
      translationKey: 'menu.categories.title',
      children: [
        {
          path: '/categories/dashboard',
          icon: 'chart-pie',
          translationKey: 'menu.categories.dashboard'
        },
        {
          path: '/categories/budgeting',
          icon: 'coins',
          translationKey: 'menu.categories.budgeting'
        },
        {
          path: '/categories/manage',
          icon: 'table',
          translationKey: 'menu.categories.manage'
        }
      ]
    },
    {
      path: '/transactions',
      icon: 'credit-card',
      translationKey: 'menu.transactions.title',
      children: [
        {
          path: '/transactions/manage',
          icon: 'table',
          translationKey: 'menu.transactions.manage'
        },
        {
          path: '/transactions/tags',
          icon: 'tag',
          translationKey: 'menu.transactions.tags'
        }
      ]
    }
  ];
}
