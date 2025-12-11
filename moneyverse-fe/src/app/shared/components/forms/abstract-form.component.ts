import {Directive, inject, input, OnInit, output} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {IconsEnum} from '../../models/icons.model';
import {FormHandler} from '../../models/form.model';

@Directive()
export abstract class AbstractFormComponent<T = unknown, D = unknown> implements OnInit {
  selectedItem = input<T | undefined>(undefined);

  onSubmit = output<D>();

  protected readonly abstract formHandler: FormHandler<T, D>;
  protected readonly fb = inject(FormBuilder);
  protected readonly icons = IconsEnum
  protected formGroup!: FormGroup;

  ngOnInit(): void {
    this.formGroup = this.formHandler.create();
  }

  getControlValue<K>(controlName: string): K {
    return this.formGroup.get(controlName)?.value;
  }

  setControlValue<K>(controlName: string, value: K, options?: { emitEvent?: boolean; onlySelf?: boolean }): void {
    this.formGroup.get(controlName)?.setValue(value, options);
  }

  disableControl(controlName: string, options?: { emitEvent?: boolean; onlySelf?: boolean }): void {
    this.formGroup.get(controlName)?.disable(options);
  }

  disableControls(controlNames: string[]): void {
    controlNames.forEach(controlName => {
      this.disableControl(controlName);
    });
  }

  enableControl(controlName: string, options?: { emitEvent?: boolean; onlySelf?: boolean }): void {
    this.formGroup.get(controlName)?.enable(options);
  }

  enableControls(controlNames: string[]): void {
    controlNames.forEach(controlName => {
      this.enableControl(controlName);
    });
  }

  markAllAsTouchedAndDirty(): void {
    Object.keys(this.formGroup.controls).forEach(key => {
      const control = this.formGroup.get(key);
      control?.markAsTouched();
      control?.markAsDirty();
    });
  }

  submit(): void {
    if (this.formGroup.valid) {
      const formData = this.prepareData();
      this.onSubmit.emit(formData);
    } else {
      this.markAllAsTouchedAndDirty();
    }
  }

  protected prepareData(): D {
    return this.formHandler.prepareData(this.formGroup);
  }

  patch(item: T): void {
    this.formHandler.patch(this.formGroup, item);
  }

  reset(): void {
    this.formHandler.reset(this.formGroup);
  }

}
