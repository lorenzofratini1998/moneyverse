import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {PreferenceKey, UserPreferenceDto} from '../models/preference.model';
import {AuthService} from '../../core/auth/auth.service';
import {computed, inject} from '@angular/core';
import {PreferenceService} from '../services/preference.service';

interface PreferenceState {
  preferences: Partial<Record<PreferenceKey, UserPreferenceDto>>;
}

export const PreferenceStore = signalStore(
  {providedIn: 'root'},

  withState<PreferenceState>(() => ({
    preferences: {}
  })),

  withMethods((store) => ({
    loadPreference(key: PreferenceKey) {
      if (store.preferences[key]) {
        return;
      }
      const preferenceService = inject(PreferenceService);
      const authService = inject(AuthService);
      preferenceService.getUserPreference(authService.getAuthenticatedUser().userId, key)
        .subscribe({
          next: (preference) => {
            const updated = {...store.preferences(), [key]: preference};
            patchState(store, {preferences: updated});
          },
          error: (err) => {
            console.error(`Preference load failed for ${key}`, err);
          }
        });

    }
  })),

  withComputed((store) => ({
    userCurrency: computed(() => store.preferences()[PreferenceKey.CURRENCY]?.value ?? 'EUR'),
    userDateFormat: computed(() => store.preferences()[PreferenceKey.DATE_FORMAT]?.value ?? 'yyyy-MM-dd'),
    userLanguage: computed(() => store.preferences()[PreferenceKey.LANGUAGE]?.value ?? 'en_US')
  })),

  withHooks({
    onInit: (store) => {
      store.loadPreference(PreferenceKey.CURRENCY);
      store.loadPreference(PreferenceKey.LANGUAGE);
      store.loadPreference(PreferenceKey.DATE_FORMAT);
    }
  })
)
