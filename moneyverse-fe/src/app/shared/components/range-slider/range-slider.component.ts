import {Component, effect, input, output, signal} from '@angular/core';
import {BoundCriteria} from '../../models/criteria.model';
import {InputNumber} from 'primeng/inputnumber';
import {FormsModule} from '@angular/forms';
import {Slider} from 'primeng/slider';

@Component({
  selector: 'app-range-slider',
  imports: [
    InputNumber,
    FormsModule,
    Slider
  ],
  templateUrl: './range-slider.component.html',
  styleUrl: './range-slider.component.scss'
})
export class RangeSliderComponent {
  title = input.required<string>();
  min = input<number>(0);
  max = input<number>(100);
  step = input<number>(1);
  minFractionDigits = input<number>(0);
  maxFractionDigits = input<number>(2);
  initialValues = input<[number, number]>();
  disabled = input<boolean>(false);

  // Output events
  onRangeChange = output<BoundCriteria>();
  onValueChange = output<[number, number]>();

  currentValues = signal<[number, number]>([0, 100]);

  constructor() {
    effect(() => {
      const initial = this.initialValues() || [this.min(), this.max()];
      this.currentValues.set([
        Math.max(this.min(), Math.min(initial[0], this.max())),
        Math.max(this.min(), Math.min(initial[1], this.max()))
      ]);
    });

    effect(() => {
      const [lower, upper] = this.currentValues();
      this.onRangeChange.emit({lower, upper});
      this.onValueChange.emit([lower, upper]);
    });
  }

  onInputNumberChange(newValue: number, index: 0 | 1): void {
    if (newValue === null || newValue === undefined) return;

    const clampedValue = Math.max(this.min(), Math.min(newValue, this.max()));
    const newValues = [...this.currentValues()] as [number, number];

    if (index === 0) {
      newValues[0] = Math.min(clampedValue, newValues[1]);
    } else {
      newValues[1] = Math.max(clampedValue, newValues[0]);
    }

    this.currentValues.set(newValues);
  }


  resetToDefaults(): void {
    this.currentValues.set([this.min(), this.max()]);
  }

  setValues(min: number, max: number): void {
    const clampedMin = Math.max(this.min(), Math.min(min, this.max()));
    const clampedMax = Math.max(this.min(), Math.min(max, this.max()));
    this.currentValues.set([
      Math.min(clampedMin, clampedMax),
      Math.max(clampedMin, clampedMax)
    ]);
  }

  getValues(): [number, number] {
    return this.currentValues();
  }
}
