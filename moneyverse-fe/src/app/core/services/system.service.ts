import {computed, effect, inject, Injectable, signal} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {PrimeNG} from 'primeng/config';
import {Language} from '../../shared/models/preference.model';
import {PreferenceStore} from '../../shared/stores/preference.store';
import {take} from 'rxjs';
import {PreferenceService} from '../../shared/services/preference.service';

@Injectable({
  providedIn: 'root'
})
export class SystemService {
  private readonly preferenceService = inject(PreferenceService);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly translateService = inject(TranslateService);
  private readonly primeNG = inject(PrimeNG);

  private readonly _languages = signal<Language[]>([]);
  private readonly _languageChanged = signal<string | null>(null);
  private readonly _currentLangIso = signal<string>('');
  private readonly _translationsReady = signal<boolean>(false);

  languages = this._languages.asReadonly();
  languageChanged = this._languageChanged.asReadonly();
  translationsReady = this._translationsReady.asReadonly();

  constructor() {
    effect(() => {
      const langs = this.languages();

      if (langs.length > 0) {
        this.setSystemLanguages(langs);
        this.setApplicationLanguage(langs);
      }
    });

  }

  setupApplication() {
    this.preferenceService.getLanguages().subscribe((languages: Language[]) => {
      const appLanguages = languages.filter(lang => lang.enabled);
      this._languages.set(appLanguages);
    });
  }

  private setSystemLanguages(languages: Language[]) {
    const isoCodes = languages.map(lang => lang.isoCode);
    this.translateService.addLangs(isoCodes);
  }

  private setApplicationLanguage(languages: Language[]) {
    const browserLang = this.translateService.getBrowserLang();
    const preferredLang = this.preferenceStore.userLanguage();
    const fallbackLang = languages.find(lang => lang.default)?.isoCode ?? 'en';

    const lang =
      preferredLang ??
      languages.find(lang => lang.isoCode === browserLang)?.isoCode ??
      fallbackLang;

    this.translateService.setDefaultLang(lang);
    this.changeLanguage(lang);
  }


  changeLanguage(lang: string) {
    this.translateService.use(lang);
    this._currentLangIso.set(lang);
    this.translateService
      .get('primeng')
      .pipe(take(1))
      .subscribe(res => {
        this.primeNG.setTranslation(res)
        this._languageChanged.set(lang);
        this._translationsReady.set(true);
      });
  }

  currentLanguage = computed<Language | null>(() => {
    const langs = this.languages();
    const currentIso = this._currentLangIso();
    return langs.find(lang => lang.isoCode === currentIso) ?? null;
  });

}
