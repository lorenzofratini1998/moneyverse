import {Component} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';
import {LucideAngularModule} from 'lucide-angular';
import {SvgComponent} from '../../../shared/components/svg/svg.component';

interface MenuItem {
  path: string;
  icon: any;
  translationKey: string;
}

@Component({
  selector: 'app-menu',
  imports: [
    RouterLinkActive,
    TranslatePipe,
    RouterLink,
    LucideAngularModule,
    SvgComponent
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
      translationKey: 'menu.accounts'
    },
    {
      path: '/categories',
      icon: 'shapes',
      translationKey: 'menu.categories'
    }
  ];
}
