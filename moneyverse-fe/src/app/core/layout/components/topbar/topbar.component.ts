import {Component, inject} from '@angular/core';
import {RouterLink} from '@angular/router';
import {StyleClass} from 'primeng/styleclass';
import {Popover} from 'primeng/popover';
import {TranslatePipe} from '@ngx-translate/core';
import {LayoutService} from '../../layout.service';
import {AuthService} from '../../../auth/auth.service';
import {
  DashboardFilterDrawerComponent
} from '../../../../features/analytics/components/dashboard-filter-drawer/dashboard-filter-drawer.component';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {OverlayBadge} from 'primeng/overlaybadge';
import {DashboardStore} from '../../../../features/analytics/services/dashboard.store';
import {DarkModeButtonComponent} from '../../../../shared/components/dark-mode-button/dark-mode-button.component';
import {LanguagePopoverComponent} from '../../../../shared/components/language-popover/language-popover.component';

@Component({
  selector: 'app-topbar',
  imports: [
    RouterLink,
    StyleClass,
    SvgComponent,
    DashboardFilterDrawerComponent,
    Popover,
    TranslatePipe,
    OverlayBadge,
    DarkModeButtonComponent,
    LanguagePopoverComponent
  ],
  templateUrl: './topbar.component.html'
})
export class TopbarComponent {

  protected readonly layoutService = inject(LayoutService);
  protected readonly dashboardStore = inject(DashboardStore);
  protected readonly authService = inject(AuthService);
  protected readonly icons = IconsEnum;


}
