import {Component, computed, input, OnDestroy, OnInit, output, signal} from '@angular/core';
import {RadioButton} from 'primeng/radiobutton';
import {DatePicker, DatePickerTypeView} from 'primeng/datepicker';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Fieldset} from 'primeng/fieldset';
import {PeriodFormat} from '../../analytics.models';
import {Subscription} from 'rxjs';

interface PeriodSelectorOption {
  id: string,
  label: string,
  value: PeriodFormat,
}

@Component({
  selector: 'app-period-selector',
  imports: [
    RadioButton,
    DatePicker,
    ReactiveFormsModule,
    Fieldset,
    FormsModule
  ],
  templateUrl: './period-selector.component.html'
})
export class PeriodSelectorComponent implements OnInit, OnDestroy {
  id = input<string>('period-selector');
  label = input<string>('Period');
  formatControlName = input<string>('format');
  periodControlName = input<string>('value');
  nullable = input<boolean>(false);
  formGroup = input.required<FormGroup>();

  onSelect = output<any>();

  private formatValueSignal = signal<PeriodFormat | null>(null);
  private subscription?: Subscription;

  ngOnInit() {
    const formatControl = this.formGroup().get(this.formatControlName());
    if (formatControl) {
      this.formatValueSignal.set(formatControl.value);
      this.subscription = formatControl.valueChanges.subscribe(value => {
        this.formatValueSignal.set(value);
      });
    }
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  options = computed<PeriodSelectorOption[]>(() => {
    const defaultOptions: PeriodSelectorOption[] = [
      {value: 'year', id: 'yearButton', label: 'Year'},
      {value: 'month', id: 'monthButton', label: 'Month'},
      {value: 'custom', id: 'customButton', label: 'Custom'}
    ]
    if (this.nullable()) {
      return [
        {value: 'none', id: 'noneButton', label: 'None'},
        ...defaultOptions
      ]
    }
    return defaultOptions
  })

  selectedFormat = computed<PeriodFormat | null>(() => {
    return this.formatValueSignal();
  });

  selectedView = computed<DatePickerTypeView>(() => {
    const format = this.selectedFormat();
    return format === 'year' ? 'year' : 'month';
  });

  placeholder = computed<string>(() => {
    switch (this.selectedFormat()) {
      case 'year':
        return 'Select year';
      case 'month':
        return 'Select month';
      default:
        return 'Select date range';
    }
  })

  getUniqueInputId(optionId: string): string {
    return `${this.id()}-${this.formatControlName()}-${optionId}`;
  }
}
