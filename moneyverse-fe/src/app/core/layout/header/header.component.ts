import {Component, effect, EventEmitter, inject, Input, Output, Signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {AuthService} from '../../auth/auth.service';
import {UserModel} from '../../auth/models/user.model';
import {RouterLink} from '@angular/router';
import {PreferenceService} from '../../../shared/services/preference.service';
import {LanguageDto} from '../../../shared/models/preference.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {LanguageService} from '../../../shared/services/language.service';
import {ThemeService} from '../../../shared/services/theme.service';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-header',
  imports: [
    FormsModule,
    TranslatePipe,
    RouterLink,
    SvgIconComponent
  ],
  templateUrl: './header.component.html'
})
export class HeaderComponent {

  protected readonly themeService = inject(ThemeService);

  @Input() pageTitle: string = '';
  @Output() toggleMenu: EventEmitter<void> = new EventEmitter();

  languages$: Signal<LanguageDto[]>;
  isDarkModeEnabled: boolean = false;
  isOpen: boolean = true;

  user: UserModel;
  selectedLanguage: LanguageDto;

  toggle(event: any) {
    event.stopPropagation();
    this.isOpen = !this.isOpen;
  }

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

  onToggleMenu() {
    this.toggleMenu.emit();
  }
}
