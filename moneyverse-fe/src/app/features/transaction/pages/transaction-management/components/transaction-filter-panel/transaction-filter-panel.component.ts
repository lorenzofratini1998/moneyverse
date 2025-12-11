import {Component, inject, viewChild} from '@angular/core';
import {FilterPanelComponent} from '../../../../../../shared/components/filter-panel/filter-panel.component';
import {TransactionFilterFormComponent} from '../transaction-filter-form/transaction-filter-form.component';
import {TransactionStore} from '../../services/transaction.store';

@Component({
  selector: 'app-transaction-filter-panel',
  imports: [
    FilterPanelComponent,
    TransactionFilterFormComponent
  ],
  template: `
    <app-filter-panel [form]="form()"
                      [activeFiltersCount]="transactionStore.activeFiltersCount()">
      <div content>
        <app-transaction-filter-form/>
      </div>
    </app-filter-panel>
  `
})
export class TransactionFilterPanelComponent {
  form = viewChild.required(TransactionFilterFormComponent);

  protected readonly transactionStore = inject(TransactionStore);
}
