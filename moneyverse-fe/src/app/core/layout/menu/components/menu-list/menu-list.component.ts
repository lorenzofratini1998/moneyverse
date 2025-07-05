import {Component, effect, input} from '@angular/core';
import {MenuItem} from '../../menu.model';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-menu-list',
  imports: [
    RouterLinkActive,
    SvgComponent,
    TranslatePipe,
    RouterLink
  ],
  templateUrl: './menu-list.component.html',
  styleUrl: './menu-list.component.scss'
})
export class MenuListComponent {
  items = input.required<MenuItem[]>();
}
