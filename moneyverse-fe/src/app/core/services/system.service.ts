import {inject, Injectable, signal} from '@angular/core';
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
  languages = this._languages.asReadonly();

  setupApplication() {
    this.preferenceService.getLanguages().subscribe((languages: Language[]) => {
      this._languages.set(languages);
      this.setSystemLanguages(languages);
      this.setApplicationLanguage(languages);
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
    this.translateService
      .get('primeng')
      .pipe(take(1))
      .subscribe(res => this.primeNG.setTranslation(res));
  }

  get currentLanguage(): Language {
    return this.languages().find(lang => lang.isoCode === this.translateService.currentLang) as Language;
  }

}
