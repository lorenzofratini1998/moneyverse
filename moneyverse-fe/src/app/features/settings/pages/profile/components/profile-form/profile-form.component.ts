import {Component, effect, inject, input} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {ReactiveFormsModule} from '@angular/forms';
import {UserModel} from '../../../../../../core/auth/models/user.model';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {SubmitButtonComponent} from '../../../../../../shared/components/forms/submit-button/submit-button.component';
import {ProfileFormHandler} from '../services/profile-form.handler';

export interface ProfileFormData {
  firstName: string;
  lastName: string;
}

@Component({
  selector: 'app-profile-form',
  imports: [
    ReactiveFormsModule,
    InputTextComponent,
    SubmitButtonComponent
  ],
  templateUrl: './profile-form.component.html',
  styleUrl: './profile-form.component.scss'
})
export class ProfileFormComponent extends AbstractFormComponent<UserModel, ProfileFormData> {

  user = input.required<UserModel>();

  protected override readonly formHandler = inject(ProfileFormHandler);

  constructor() {
    super();
    effect(() => {
      this.patch(this.user());
    })
  }


}
