import {Component, inject} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {Budget} from '../../../../category.model';
import {BudgetFormHandler} from '../../services/budget-form.handler';
import {ReactiveFormsModule} from '@angular/forms';
import {DatePickerComponent} from '../../../../../../shared/components/forms/date-picker/date-picker.component';
import {
  AmountInputNumberComponent
} from '../../../../../../shared/components/forms/amount-input-number/amount-input-number.component';
import {
  CurrencySelectComponent
} from '../../../../../../shared/components/forms/currency-select/currency-select.component';
import {
  CategorySelectComponent
} from '../../../../../../shared/components/forms/category-select/category-select.component';
import {BudgetFormData} from '../../models/form.models';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-budget-form',
  imports: [
    ReactiveFormsModule,
    DatePickerComponent,
    AmountInputNumberComponent,
    CurrencySelectComponent,
    CategorySelectComponent,
    TranslatePipe
  ],
  templateUrl: './budget-form.component.html'
})
export class BudgetFormComponent extends AbstractFormComponent<Budget, BudgetFormData> {

  protected override formHandler = inject(BudgetFormHandler);

}
