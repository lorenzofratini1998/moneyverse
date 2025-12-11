import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../../shared/models/form.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {PreferenceKey, UserPreferenceFormData} from '../../../../../../shared/models/preference.model';

@Injectable({
  providedIn: 'root'
})
export class PreferenceFormHandler implements FormHandler<any, UserPreferenceFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly preferenceStore = inject(PreferenceStore);

  create(): FormGroup {
    return this.fb.group({
      currency: [null, Validators.required],
      language: [null, Validators.required],
      dateFormat: [null, Validators.required],
    });
  }

  patch(form: FormGroup, data: any): void {
    form.patchValue({
      currency: this.preferenceStore.userCurrency(),
      language: this.preferenceStore.userLanguage(),
      dateFormat: this.preferenceStore.userDateFormat(),
    });
  }

  reset(form: FormGroup): void {
    throw new Error("Method not implemented.");
  }

  prepareData(form: FormGroup): UserPreferenceFormData {
    const value = form.value;
    return {
      language: this.preferenceStore.userLanguage() !== value.language ? {
        key: PreferenceKey.LANGUAGE,
        value: value.language
      } : undefined,
      dateFormat: this.preferenceStore.userDateFormat() !== value.dateFormat ? {
        key: PreferenceKey.DATE_FORMAT,
        value: value.dateFormat
      } : undefined,
    }
  }

}
