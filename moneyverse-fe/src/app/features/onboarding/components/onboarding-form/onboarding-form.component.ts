import {Component, computed, effect, inject, input, output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {
  DateFormat,
  Language,
  PreferenceKey,
  UserPreference,
  UserPreferenceFormData
} from "../../../../shared/models/preference.model";
import {CurrencySelectComponent} from '../../../../shared/components/forms/currency-select/currency-select.component';
import {SelectComponent} from '../../../../shared/components/forms/select/select.component';
import {SubmitButtonComponent} from '../../../../shared/components/forms/submit-button/submit-button.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-onboarding-form',
  imports: [
    ReactiveFormsModule,
    CurrencySelectComponent,
    SelectComponent,
    SubmitButtonComponent,
    TranslatePipe
  ],
  templateUrl: './onboarding-form.component.html'
})
export class OnboardingFormComponent {

  languages = input.required<Language[]>();
  dateFormats = input.required<DateFormat[]>();
  userPreferences = input.required<UserPreference[]>();

  onSubmit = output<UserPreferenceFormData>();

  userCurrency = computed(() => this.userPreferences().find(pref => pref.preference.name === PreferenceKey.CURRENCY));
  userLanguage = computed(() => this.userPreferences().find(pref => pref.preference.name === PreferenceKey.LANGUAGE));
  userDateFormat = computed(() => this.userPreferences().find(pref => pref.preference.name === PreferenceKey.DATE_FORMAT));

  protected readonly formGroup: FormGroup;
  private readonly fb = inject(FormBuilder);


  constructor() {
    this.formGroup = this.createForm();
    effect(() => this.patchForm());
  }

  patchForm(): void {
    this.formGroup.patchValue({
      currency: this.userCurrency()?.value ?? null,
      language: this.userLanguage()?.value ?? null,
      dateFormat: this.userDateFormat()?.value ?? null
    })
  }

  protected createForm(): FormGroup {
    return this.fb.group({
      currency: [null, Validators.required],
      language: [null, Validators.required],
      dateFormat: [null, Validators.required]
    })
  }

  submit() {
    const formValue = this.formGroup.value;
    if (this.formGroup.valid) {
      this.onSubmit.emit({
        currency: {
          key: PreferenceKey.CURRENCY,
          value: formValue.currency
        },
        language: {
          key: PreferenceKey.LANGUAGE,
          value: formValue.language
        },
        dateFormat: {
          key: PreferenceKey.DATE_FORMAT,
          value: formValue.dateFormat
        }
      })
    }
  }
}

