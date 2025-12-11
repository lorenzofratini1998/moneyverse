import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {PreferenceKey, UserPreference, UserPreferenceRequest} from '../models/preference.model';
import {AuthService} from '../../core/auth/auth.service';
import {computed, inject} from '@angular/core';
import {PreferenceService} from '../services/preference.service';
import {ToastService} from '../services/toast.service';
import {TranslationService} from '../services/translation.service';

interface PreferenceState {
  preferences: Partial<Record<PreferenceKey, UserPreference>>;
}

export const PreferenceStore = signalStore(
  {providedIn: 'root'},

  withState<PreferenceState>(() => ({
    preferences: {}
  })),

  withMethods((store) => {
    const preferenceService = inject(PreferenceService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    return {
      loadPreference(key: PreferenceKey) {
        if (store.preferences[key]) {
          return;
        }

        preferenceService.getUserPreference(authService.user().userId, key)
          .subscribe({
            next: (preference) => {
              const updated = {...store.preferences(), [key]: preference};
              patchState(store, {preferences: updated});
            },
            error: (err) => {
              console.error(`Preference load failed for ${key}`, err);
            }
          });
      },

      loadPreferences() {
        preferenceService.getUserPreferences(authService.user().userId)
          .subscribe({
            next: (preferences) => {
              const preferencesMap = preferences.reduce((acc, pref) => {
                const key = pref.preference.name as PreferenceKey;
                acc[key] = pref;
                return acc;
              }, {} as Partial<Record<PreferenceKey, UserPreference>>)

              patchState(store, {preferences: preferencesMap});
            }
          })
      },

      updateUserPreferences(preferences: UserPreference[]) {
        const currentPreferences = store.preferences();
        const updatedPreferences = preferences.reduce((acc, pref) => {
          const key = pref.preference.name as PreferenceKey;
          acc[key] = pref;
          return acc;
        }, {...currentPreferences});

        patchState(store, {preferences: updatedPreferences});
        toastService.success(translateService.translate('app.message.preferences.update.success'));
      },

      updatePreference(key: PreferenceKey, preference: UserPreference) {
        const request: UserPreferenceRequest[] = [{
          preferenceId: preference.preference.preferenceId,
          value: preference.value
        }];

        preferenceService.updateUserPreferences(preference.userId, request)
          .subscribe({
            next: (data) => {
              const updatedPreference = data[0];

              const updated = {
                ...store.preferences(),
                [key]: updatedPreference,
              };

              patchState(store, { preferences: updated });
            },
            error: (err) => {
              toastService.error(translateService.translate('app.message.preferences.update.error'));
            }
          })
      },

      removePreference(key: PreferenceKey) {
        const updated = {...store.preferences()};
        delete updated[key];
        patchState(store, {preferences: updated});
      },

      reset() {
        patchState(store, {preferences: {}});
      }
    }
  }),

  withComputed((store) => ({
    userCurrency: computed(() => store.preferences()[PreferenceKey.CURRENCY]?.value ?? 'EUR'),
    userDateFormat: computed(() => store.preferences()[PreferenceKey.DATE_FORMAT]?.value ?? 'yyyy-MM-dd'),
    userLanguage: computed(() => store.preferences()[PreferenceKey.LANGUAGE]?.value ?? 'en'),
    hasPreference: computed(() => (key: PreferenceKey) => !!store.preferences()[key]),
    allPreferences: computed(() => store.preferences()),
    preferencesCount: computed(() => Object.keys(store.preferences()).length)
  })),

  withHooks({
    onInit: (store) => {
      store.loadPreferences();
    }
  })
)
