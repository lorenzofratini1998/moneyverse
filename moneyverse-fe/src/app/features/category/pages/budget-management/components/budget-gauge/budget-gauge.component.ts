import {Component, input} from '@angular/core';
import {GaugeComponent} from '../../../../../../shared/components/charts/gauge/gauge.component';
import {Budget} from '../../../../category.model';

@Component({
  selector: 'app-budget-gauge',
  imports: [
    GaugeComponent
  ],
  template: `
    <app-gauge [data]="{value: budget().amount}"
               [max]="budget().budgetLimit"
               [currency]="budget().currency"
    />`,
})
export class BudgetGaugeComponent {
  budget = input.required<Budget>();
}
