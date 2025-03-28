import {Component} from '@angular/core';
import {SvgIconComponent} from 'angular-svg-icon';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';
import {HomeIcon, LayersIcon, LucideAngularModule} from 'lucide-angular';

interface MenuItem {
  path: string;
  icon: any;
  translationKey: string;
}

@Component({
  selector: 'app-menu',
  imports: [
    SvgIconComponent,
    RouterLinkActive,
    TranslatePipe,
    RouterLink,
    LucideAngularModule
  ],
  templateUrl: './menu.component.html'
})
export class MenuComponent {

  menuItems: MenuItem[] = [
    {
      path: '/overview',
      icon: HomeIcon,
      translationKey: 'menu.overview'
    },
    {
      path: '/accounts',
      icon: LayersIcon,
      translationKey: 'menu.accounts'
    }
  ];
}
