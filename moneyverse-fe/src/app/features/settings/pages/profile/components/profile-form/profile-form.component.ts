import {Component, computed, effect, inject, input, output} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserModel} from '../../../../../../core/auth/models/user.model';

@Component({
  selector: 'app-profile-form',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './profile-form.component.html'
})
export class ProfileFormComponent {
  private readonly fb = inject(NonNullableFormBuilder);

  user = input.required<UserModel>();
  formSubmit = output<any>();
  initialLetters = computed(() => this.user().firstName[0] + this.user().lastName[0]);

  profileForm = this.fb.group({
    firstName: ["", Validators.required],
    lastName: ["", Validators.required],
    email: ["", [Validators.required, Validators.email]],
  });

  constructor() {
    effect(() => {
      this.patchForm(this.user());
    })
  }

  private patchForm(user: UserModel): void {
    this.profileForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email
    })
  }

  save() {
    this.formSubmit.emit(this.profileForm.value);
  }
}
