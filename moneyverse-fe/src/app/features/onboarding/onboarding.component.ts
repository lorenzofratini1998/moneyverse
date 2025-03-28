import {Component, computed, inject, signal} from '@angular/core';
import {AuthService} from '../../core/auth/auth.service';
import {FormsModule} from '@angular/forms';
import {CurrencyService} from '../../shared/services/currency.service';
import {map, switchMap} from 'rxjs';
import {PreferenceService} from '../../shared/services/preference.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {Router} from '@angular/router';
import {DATE_FORMATS, PreferenceKey, UserPreferenceRequestDto} from '../../shared/models/preference.model';
import {StorageService} from '../../shared/services/storage.service';
import {OnboardingFormComponent} from './components/onboarding-form/onboarding-form.component';
import {LucideAngularModule} from 'lucide-angular';
import {STORAGE_MISSING_MANDATORY_PREFERENCES} from '../../shared/models/constants.model';
import {MessageService} from '../../shared/services/message.service';
import {ToastComponent, ToastEnum} from '../../shared/components/toast/toast.component';
import {OnboardingHeaderComponent} from './components/onboarding-header/onboarding-header.component';

@Component({
  selector: 'app-onboarding',
  imports: [
    FormsModule,
    OnboardingFormComponent,
    LucideAngularModule,
    OnboardingHeaderComponent,
    ToastComponent,

  ],
  templateUrl: './onboarding.component.html'
})
export class OnboardingComponent {
  private readonly authService = inject(AuthService);
  private readonly currencyService = inject(CurrencyService);
  private readonly preferenceService = inject(PreferenceService);
  private readonly storageService = inject(StorageService);
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);

  currencies$ = toSignal(
    this.currencyService.getCurrencies().pipe(
      map(currencies => currencies.map(curr => ({...curr, label: `${curr.name} (${curr.code})`, value: curr.code})))
    ),
    {
      initialValue: []
    });
  languages$ = toSignal(this.preferenceService.getLanguages().pipe(
      map(languages => languages.map(lang => ({...lang, label: lang.country, value: lang.isoCode})))
    ),
    {
      initialValue: []
    });
  dateFormats$ = signal(DATE_FORMATS);

  mandatoryPreferences$ = toSignal(
    this.preferenceService.getPreferences(true),
    {initialValue: []}
  )
  userPreferences$ = toSignal(
    this.authService.getUserId().pipe(
      switchMap(userId => this.preferenceService.getUserPreferences(userId))
    ),
    {initialValue: []}
  );

  userCurrency$ = computed(() => this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.CURRENCY));
  userLanguage$ = computed(() => this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.LANGUAGE));
  userDateFormat$ = computed(() => this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.DATE_FORMAT));
  missingPreferences$ = computed(() => this.mandatoryPreferences$().filter(pref => !this.userPreferences$().some(userPref => userPref.preference.name === pref.name)));

  save(formValue: any) {
    const request: UserPreferenceRequestDto[] = this.missingPreferences$().map(preference => {
      const key = preference.name as PreferenceKey;
      return {
        preferenceId: preference.preferenceId,
        value: formValue[key]
      };
    })
    if (Object.keys(request).length === 0) {
      void this.router.navigateByUrl('/');
    }
    this.authService.getUserId().pipe(
      switchMap(userId => this.preferenceService.saveUserPreferences(userId, request))
    ).subscribe({
      next: () => {
        this.storageService.clear();
        this.storageService.setItem(STORAGE_MISSING_MANDATORY_PREFERENCES, 'false');
        void this.router.navigateByUrl('/');
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: 'Error saving preferences'
        });
      }
    })
  }
}
