import {Component, computed, inject} from '@angular/core';
import {AuthService} from '../../core/auth/auth.service';
import {FormsModule} from '@angular/forms';
import {PreferenceService} from '../../shared/services/preference.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {Router} from '@angular/router';
import {
  DATE_FORMATS,
  STORAGE_MISSING_MANDATORY_PREFERENCES,
  UserPreferenceFormData,
  UserPreferenceRequest
} from '../../shared/models/preference.model';
import {StorageService} from '../../shared/services/storage.service';
import {OnboardingFormComponent} from './components/onboarding-form/onboarding-form.component';
import {LucideAngularModule} from 'lucide-angular';
import {Card} from 'primeng/card';
import {PreferenceStore} from '../../shared/stores/preference.store';
import {ToastService} from '../../shared/services/toast.service';
import {SystemService} from '../../core/services/system.service';
import {TranslatePipe} from '@ngx-translate/core';
import {TranslationService} from '../../shared/services/translation.service';

@Component({
  selector: 'app-onboarding',
  imports: [
    FormsModule,
    OnboardingFormComponent,
    LucideAngularModule,
    Card,
    TranslatePipe,


  ],
  templateUrl: './onboarding.component.html'
})
export class OnboardingComponent {
  protected readonly systemService = inject(SystemService);
  private readonly authService = inject(AuthService);
  private readonly preferenceService = inject(PreferenceService);
  private readonly storageService = inject(StorageService);
  private readonly toastService = inject(ToastService);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly router = inject(Router);
  private readonly translateService = inject(TranslationService);

  protected readonly dateFormats = DATE_FORMATS;

  mandatoryPreferences = toSignal(
    this.preferenceService.getPreferences(true),
    {initialValue: []}
  )

  userPreferences = toSignal(
    this.preferenceService.getUserPreferences(this.authService.user().userId),
    {initialValue: []}
  );

  missingPreferences = computed(() => this.mandatoryPreferences().filter(pref =>
    !this.userPreferences().some(userPref =>
      userPref.preference.name === pref.name)));

  submit(formValue: UserPreferenceFormData) {
    const request = this.mapFormDataToRequest(formValue);

    if (request.length === 0) {
      this.navigateToHome();
      return;
    }

    this.preferenceService.saveUserPreferences(this.authService.user().userId, request).subscribe({
      next: (result) => {
        this.preferenceStore.updateUserPreferences(result);
        this.navigateToHome();
      },
      error: () => {
        this.toastService.error(this.translateService.translate('app.message.preferences.save.error'));
      }
    })
  }

  private mapFormDataToRequest(formValue: UserPreferenceFormData): UserPreferenceRequest[] {
    const missingPrefs = this.missingPreferences();
    const request: UserPreferenceRequest[] = [];

    Object.entries(formValue).forEach(([_, formData]) => {
      const preference = missingPrefs.find(pref => pref.name === formData.key);
      if (preference) {
        request.push({
          preferenceId: preference.preferenceId,
          value: formData.value
        });
      }
    })
    return request;
  }

  private navigateToHome() {
    this.storageService.clear();
    this.storageService.setItem(STORAGE_MISSING_MANDATORY_PREFERENCES, {
      userId: this.authService.user().userId,
      value: false,
    });
    void this.router.navigateByUrl('/');
  }
}
