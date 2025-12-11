import {FormGroup} from '@angular/forms';

export interface FormHandler<T, D> {
  create(): FormGroup;

  patch(form: FormGroup, data: T): void;

  reset(form: FormGroup): void;

  prepareData(form: FormGroup): D;
}
