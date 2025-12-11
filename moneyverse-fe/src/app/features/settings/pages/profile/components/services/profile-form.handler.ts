import {inject, Injectable} from '@angular/core';
import {UserModel} from '../../../../../../core/auth/models/user.model';
import {ProfileFormData} from '../profile-form/profile-form.component';
import {FormHandler} from '../../../../../../shared/models/form.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ProfileFormHandler implements FormHandler<UserModel, ProfileFormData> {
  private readonly fb = inject(FormBuilder);

  create(): FormGroup {
    return this.fb.group({
      firstName: [null, Validators.required],
      lastName: [null, Validators.required],
      email: [null, [Validators.required, Validators.email]]
    });
  }

  patch(form: FormGroup, user: UserModel): void {
    form.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email
    })
  }

  reset(form: FormGroup): void {
    form.reset({
      firstName: null,
      lastName: null,
      email: null
    })
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): ProfileFormData {
    const value = form.value;
    return {
      firstName: value.firstName,
      lastName: value.lastName,
      email: value.email
    };
  }

}
