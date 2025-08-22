import {Component, effect, inject, signal} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {ReactiveFormsModule} from '@angular/forms';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {LucideAngularModule} from 'lucide-angular';
import {SelectComponent} from '../../../../../../shared/components/forms/select/select.component';
import {DATE_FORMATS, UserPreferenceFormData} from '../../../../../../shared/models/preference.model';
import {SystemService} from '../../../../../../core/services/system.service';
import {PreferenceFormHandler} from '../services/preference-form.handler';
import {SubmitButtonComponent} from '../../../../../../shared/components/forms/submit-button/submit-button.component';

@Component({
  selector: 'app-preference-form',
  imports: [
    ReactiveFormsModule,
    InputTextComponent,
    LucideAngularModule,
    SelectComponent,
    SubmitButtonComponent
  ],
  templateUrl: './preference-form.component.html'
})
export class PreferenceFormComponent extends AbstractFormComponent<any, UserPreferenceFormData> {

  protected override readonly formHandler = inject(PreferenceFormHandler);
  protected readonly systemService = inject(SystemService);

  dateFormats = signal(DATE_FORMATS);

  constructor() {
    super();
    effect(() => {
      this.patch('Test');
    });
  }
}
