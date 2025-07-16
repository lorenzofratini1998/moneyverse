import {FormGroup} from '@angular/forms';

export function isInvalid(formGroup: FormGroup, controlName: string) {
  const control = formGroup.get(controlName);
  return !!control && control.invalid && (control.dirty || control.touched);
}
