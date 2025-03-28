import {Component, effect, inject, input, output} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {LockKeyholeIcon, LucideAngularModule} from 'lucide-angular';
import {DateFormat, LanguageDto, UserPreferenceDto} from '../../../../../../shared/models/preference.model';

@Component({
  selector: 'app-preference-form',
  imports: [
    ReactiveFormsModule,
    LucideAngularModule
  ],
  templateUrl: './preference-form.component.html',
  styleUrl: './preference-form.component.scss'
})
export class PreferenceFormComponent {

  protected readonly LockKeyholeIcon = LockKeyholeIcon;
  private readonly fb = inject(NonNullableFormBuilder);
  userCurrency = input.required<UserPreferenceDto>();
  userLanguage = input.required<UserPreferenceDto>();
  userDateFormat = input.required<UserPreferenceDto>();
  languages = input.required<LanguageDto[]>();
  dateFormats = input.required<DateFormat[]>();
  formSubmit = output<any>();

  preferenceForm = this.fb.group({
    currency: [{value: '', disabled: true}, Validators.required],
    language: ['', Validators.required],
    dateFormat: ['', Validators.required]
  });

  constructor() {
    effect(() => {
      this.preferenceForm.patchValue({
        currency: this.userCurrency()?.value ?? '',
        language: this.userLanguage()?.value ?? '',
        dateFormat: this.userDateFormat()?.value ?? ''
      });
    });
  }

  save() {
    this.formSubmit.emit(this.preferenceForm.value);
  }
}
