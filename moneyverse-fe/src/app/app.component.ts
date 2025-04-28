import {Component} from '@angular/core';
import {RouterModule} from '@angular/router';
import {PreferenceService} from './shared/services/preference.service';
import {AuthService} from './core/auth/auth.service';
import {LanguageService} from './shared/services/language.service';
import {ThemeService} from './shared/services/theme.service';
import {LoadingService} from './shared/services/loading.service';

@Component({
  selector: 'app-root',
  imports: [RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [PreferenceService]
})
export class AppComponent {

  constructor(private readonly authService: AuthService,
              private readonly languageService: LanguageService,
              private readonly themeService: ThemeService,
              readonly loadingService: LoadingService
  ) {
    this.languageService.useLanguages();
    this.authService.getUserId().subscribe(userId => this.languageService.setLanguage(userId));
    this.themeService.setTheme('light');
  }
}
