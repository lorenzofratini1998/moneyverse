import {AbstractControl, ValidationErrors} from '@angular/forms';
import {Directive, input} from '@angular/core';
import {AbstractFormControl} from './AbstractFormControl.components';

@Directive()
export abstract class AbstractTextComponent extends AbstractFormControl<string> {

  maxLength = input<number>(255)
  readonly = input<boolean>(false)

  onInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.value = target.value;
    this.onChange(this.value);
  }

  onBlur(event: Event) {
    this.onTouched();
  }

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    const errors: ValidationErrors = {};

    if (this.required() && (!this.value || this.value.trim() === '')) {
      errors['required'] = true;
    }

    if (this.value && this.value.length > this.maxLength()) {
      errors['maxlength'] = {
        requiredLength: this.maxLength(),
        actualLength: this.value.length
      };
    }

    return Object.keys(errors).length > 0 ? errors : null;
  }

  override get errorMessage(): string {
    return this.errorService.getErrorMessage(
      this.control,
      this.label(),
      this.maxLength()
    );
  }

}
