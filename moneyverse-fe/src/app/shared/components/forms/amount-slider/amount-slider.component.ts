import {Component, effect, forwardRef, input} from '@angular/core';
import {AbstractFormControl} from '../AbstractFormControl.components';
import {AbstractControl, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors} from '@angular/forms';
import {Slider} from 'primeng/slider';

@Component({
  selector: 'app-amount-slider',
  imports: [
    Slider,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AmountSliderComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AmountSliderComponent),
      multi: true
    }
  ],
  templateUrl: './amount-slider.component.html'
})
export class AmountSliderComponent extends AbstractFormControl<number[]> {
  min = input<number>(0);
  max = input<number>(100);
  fractionDigits = input<number>(2);
  step = input<number>(0.01);

  constructor() {
    super();
    effect(() => {
      this.value = [this.min(), this.max()]
      this.onChange(this.value);
    });
  }

  override writeValue(obj: number[] | null): void {
    if (!obj || obj.every(v => v === null)) {
      this.value = [this.min(), this.max()].map(v => this.roundValue(v));
      this.onChange(this.value);
    } else {
      this.value = obj.map(v => v !== null ? this.roundValue(v) : v) as number[];
    }
  }

  onSliderChange(newValue: number[]): void {
    this.value = [this.roundValue(newValue[0]), this.roundValue(newValue[1])];
    this.onChange(this.value);
  }

  onSliderSlideEnd(): void {
    this.onTouched();
  }

  override validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    const val = this.value;

    if (this.required() && !val) {
      return {required: true};
    }
    if (!val) return null;

    const [lower, upper] = val;
    const errors: ValidationErrors = {};

    if (lower < this.min() || lower > this.max()) {
      errors['lowerOutOfRange'] = true;
    }
    if (upper < this.min() || upper > this.max()) {
      errors['upperOutOfRange'] = true;
    }
    if (lower > upper) {
      errors['invalidRange'] = true;
    }

    return Object.keys(errors).length ? errors : null;
  }

  private roundValue(value: number): number {
    const factor = Math.pow(10, this.fractionDigits());
    return Math.round(value * factor) / factor;
  }
}
