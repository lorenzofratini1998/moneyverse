import {Component, effect, inject, input, output} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {CurrencyDto} from "../../../../shared/models/currencyDto";
import {DateFormat, LanguageDto, UserPreferenceDto} from "../../../../shared/models/preference.model";

@Component({
  selector: 'app-onboarding-form',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './onboarding-form.component.html'
})
export class OnboardingFormComponent {

  private readonly fb = inject(NonNullableFormBuilder);
  currencies = input.required<CurrencyDto[]>()
  languages = input.required<LanguageDto[]>()
  dateFormats = input.required<DateFormat[]>()
  userCurrency = input<UserPreferenceDto>()
  userLanguage = input<UserPreferenceDto>()
  userDateFormat = input<UserPreferenceDto>()
  formSubmit = output<any>();

  onboardingForm = this.fb.group({
    CURRENCY: ['', Validators.required],
    LANGUAGE: ['', Validators.required],
    DATE_FORMAT: ['', Validators.required]
  })

  constructor() {
    effect(() => {
      this.onboardingForm.patchValue({
        CURRENCY: this.userCurrency()?.value ?? this.currencies().find(curr => curr.default)?.code ?? '',
        LANGUAGE: this.userLanguage()?.value ?? this.languages().find(lang => lang.default)?.isoCode ?? '',
        DATE_FORMAT: this.userDateFormat()?.value ?? this.dateFormats().find(format => format.default)?.value ?? ''
      });
    });
  }

  save() {
    this.formSubmit.emit(this.onboardingForm.value);
  }
}

