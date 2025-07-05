import {Component, effect, EventEmitter, Input, Output, Signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {AuthService} from '../../auth/auth.service';
import {UserModel} from '../../auth/models/user.model';
import {RouterLink} from '@angular/router';
import {PreferenceService} from '../../../shared/services/preference.service';
import {LanguageDto} from '../../../shared/models/preference.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {LanguageService} from '../../../shared/services/language.service';
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {DarkModeToggleComponent} from '../../../shared/components/dark-mode-toggle/dark-mode-toggle.component';
import {ICONS, IconsEnum} from '../../../shared/models/icons.model';
import {
  DashboardFilterDrawerComponent
} from '../../../shared/components/dashboard-filter-drawer/dashboard-filter-drawer.component';
import {
  DashboardFilterPanelComponent
} from '../../../shared/components/dashboard-filter-panel/dashboard-filter-panel.component';

@Component({
  selector: 'app-header',
  imports: [
    FormsModule,
    TranslatePipe,
    RouterLink,
    SvgComponent,
    DarkModeToggleComponent,
    DashboardFilterDrawerComponent,
    DashboardFilterPanelComponent
  ],
  templateUrl: './header.component.html'
})
export class HeaderComponent {

  @Input() pageTitle: string = '';
  @Output() toggleMenu: EventEmitter<void> = new EventEmitter();

  languages$: Signal<LanguageDto[]>;

  user: UserModel;
  selectedLanguage: LanguageDto;

  constructor(authService: AuthService,
              private readonly preferenceService: PreferenceService,
              private readonly languageService: LanguageService) {
    this.user = authService.getAuthenticatedUser();
    this.languages$ = toSignal(this.preferenceService.getLanguages(), {initialValue: []});
    this.selectedLanguage = {isoCode: this.languageService.getCurrentLanguage()} as LanguageDto;
    effect(() => {
      if (this.languages$().length > 0) {
        this.selectedLanguage = this.languages$().find(lang => lang.isoCode === this.languageService.getCurrentLanguage()) || this.selectedLanguage;
      }
    });
  }

  changeLanguage(lang: LanguageDto): void {
    this.selectedLanguage = lang;
    this.languageService.useLanguage(lang.isoCode);
  }

  protected readonly Icons = ICONS;
  protected readonly IconsEnum = IconsEnum;
}
