import {AbstractControl, FormGroup} from '@angular/forms';

export function isInvalidOld(formGroup: FormGroup, controlName: string) {
  const control = formGroup.get(controlName);
  return !!control && control.invalid && (control.dirty || control.touched);
}

export function isInvalid(control: AbstractControl | null) {
  return !!(control && control.invalid && (control.dirty || control.touched));
}
