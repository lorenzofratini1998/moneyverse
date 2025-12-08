import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {PreferenceFormComponent} from './components/preference-form/preference-form.component';
import {Card} from 'primeng/card';
import {PreferenceStore} from '../../../../shared/stores/preference.store';
import {PreferenceService} from '../../../../shared/services/preference.service';
import {UserPreferenceFormData, UserPreferenceRequest} from '../../../../shared/models/preference.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {AuthService} from '../../../../core/auth/auth.service';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-preference',
  imports: [
    FormsModule,
    PreferenceFormComponent,
    Card,
    TranslatePipe,

  ],
  template: `
    <p-card>
      <ng-template #title>
        <h3>{{ 'app.preferences' | translate }}</h3>
      </ng-template>
      <ng-template #subtitle>
        <p>{{ 'app.features.preferences.subtitle' | translate }}</p>
      </ng-template>
      <div class="form-dialog-content-container">
        <app-preference-form (onSubmit)="savePreferences($event)"/>
      </div>
    </p-card>
  `
})
export class PreferenceComponent {
  private readonly authService = inject(AuthService);
  private readonly preferenceService = inject(PreferenceService);
  private readonly preferenceStore = inject(PreferenceStore);

  systemPreferences = toSignal(
    this.preferenceService.getPreferences(),
    {initialValue: []}
  )

  savePreferences(formData: UserPreferenceFormData) {
    const request = this.mapFormDataToRequest(formData);
    if (request.length === 0) {
      return
    }
    this.preferenceService.updateUserPreferences(this.authService.user().userId, request).subscribe({
        next: (result) => {
          this.preferenceStore.updateUserPreferences(result);
        },
        error: () => {
          console.log('Error saving preferences');
        }
      }
    )

  }

  private mapFormDataToRequest(formValue: UserPreferenceFormData): UserPreferenceRequest[] {
    const preferences = this.systemPreferences();
    const request: UserPreferenceRequest[] = [];

    Object.entries(formValue).forEach(([_, formData]) => {
      const preference = preferences.find(pref => formData && pref.name === formData.key);
      if (preference) {
        request.push({
          preferenceId: preference.preferenceId,
          value: formData.value
        });
      }
    })
    return request;
  }
}
