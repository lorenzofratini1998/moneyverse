import {Component, forwardRef, input} from '@angular/core';
import {DatePicker, DatePickerTypeView} from 'primeng/datepicker';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {
  AbstractControl,
  FormsModule,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule,
  ValidationErrors
} from '@angular/forms';
import {AbstractFormControl} from '../AbstractFormControl.components';
import {LabelComponent} from '../label/label.component';

type SelectionMode = "range" | "single" | "multiple";

@Component({
  selector: 'app-date-picker',
  imports: [
    DatePicker,
    FloatLabel,
    Message,
    ReactiveFormsModule,
    FormsModule,
    LabelComponent
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => DatePickerComponent),
      multi: true
    }
  ],
  templateUrl: './date-picker.component.html',
  styleUrl: './date-picker.component.scss'
})
export class DatePickerComponent extends AbstractFormControl<Date | Date[]> {
  selectionMode = input<SelectionMode>('single');
  showIcon = input<boolean>(true);
  dateFormat = input<string>('dd/mm/yy');
  readonlyInput = input<boolean>(true);
  placeholder = input<string | undefined>(undefined);
  view = input<DatePickerTypeView>('date')

  onDateChange(selectedDates: Date | Date[]): void {
    this.value = selectedDates;
    this.onChange(selectedDates);
    this.onTouched();
  }

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (this.value === null || this.value === undefined)) {
      return {required: true};
    }
    return null
  }

}
