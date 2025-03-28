import {Component, computed, inject, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {PreferenceService} from '../../../../shared/services/preference.service';
import {AuthService} from '../../../../core/auth/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {map, shareReplay, switchMap} from 'rxjs';
import {DATE_FORMATS, PreferenceKey, UserPreferenceDto} from '../../../../shared/models/preference.model';
import {PreferenceFormComponent} from './components/preference-form/preference-form.component';

@Component({
  selector: 'app-preferences',
  imports: [
    FormsModule,
    PreferenceFormComponent,
  ],
  templateUrl: './preferences.component.html',
  standalone: true,
  styleUrl: './preferences.component.scss'
})
export class PreferencesComponent {
  private readonly userService = inject(PreferenceService);
  private readonly authService = inject(AuthService);

  private readonly userId$ = this.authService.getUserId().pipe(shareReplay(1));
  languages$ = toSignal(
    this.userService.getLanguages(), {initialValue: []}
  );
  dateFormats$ = signal(DATE_FORMATS);
  userPreferences$ = toSignal(
    this.userId$.pipe(
      switchMap(userId => this.userService.getUserPreferences(userId))
    ), {initialValue: []}
  );

  userCurrency$ = computed(() => {
    return this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.CURRENCY);
  })

  userLanguage$ = computed(() => {
    return this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.LANGUAGE);
  });

  userDateFormat$ = computed(() => {
    return this.userPreferences$().find(pref => pref.preference.name === PreferenceKey.DATE_FORMAT);
  });


  submit(formValue: any) {
    console.log(formValue);
  }
}
