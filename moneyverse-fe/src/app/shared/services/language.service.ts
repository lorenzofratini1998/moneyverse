import {inject, Injectable} from '@angular/core';
import {PreferenceService} from './preference.service';
import {TranslateService} from '@ngx-translate/core';
import {LanguageDto, PreferenceKey} from '../models/preference.model';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly preferenceService = inject(PreferenceService);
  private readonly translateService = inject(TranslateService);

  useLanguages(): void {
    this.preferenceService.getLanguages().subscribe((languages: LanguageDto[]) => {
      this.translateService.addLangs(languages.map(lang => lang.isoCode));
    })
  }

  setLanguage(userId: string) {
    this.preferenceService.getUserPreference(userId, PreferenceKey.LANGUAGE).subscribe(userLang => {
      this.translateService.setDefaultLang(userLang.value);
      this.translateService.use(userLang.value);
    })
  }

  useLanguage(isoCode: string) {
    this.translateService.use(isoCode);
  }

  getCurrentLanguage(): string {
    return this.translateService.currentLang
  }
}
