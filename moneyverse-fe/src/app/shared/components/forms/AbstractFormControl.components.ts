import {Directive, input} from '@angular/core';
import {AbstractControl, ControlValueAccessor, ValidationErrors, Validator} from '@angular/forms';

@Directive()
export abstract class AbstractFormControl<T> implements ControlValueAccessor, Validator {

  id = input<string>('')
  label = input<string>('')
  required = input<boolean>(false)
  disabled = input<boolean>(false)

  protected value: T | null = null
  protected control: AbstractControl | null = null

  protected onChange = (value: any) => {
  }
  protected onTouched = () => {
  }

  abstract validate(control: AbstractControl): ValidationErrors | null;

  writeValue(obj: any): void {
    this.value = obj;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  protected get showError(): boolean {
    return this.required() && this.isInvalid();
  }

  private isInvalid() {
    return !!(this.control && this.control.invalid && (this.control.dirty || this.control.touched));
  }
}
