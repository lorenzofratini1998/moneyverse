import {Component, computed, inject, input, viewChild} from '@angular/core';
import {Account} from '../../../../account.model';
import {ReactiveFormsModule} from '@angular/forms';
import {FilterPanelComponent} from '../../../../../../shared/components/filter-panel/filter-panel.component';
import {AccountFilterFormComponent} from '../account-filter-form/account-filter-form.component';
import {AccountFilterStore} from '../../services/account-filter.store';

@Component({
  selector: 'app-account-filter-panel',
  imports: [
    ReactiveFormsModule,
    FilterPanelComponent,
    AccountFilterFormComponent
  ],
  template: `
    <app-filter-panel [form]="form()"
                      [activeFiltersCount]="accountFilterStore.activeFiltersCount()">
      <div content>
        <app-account-filter-form [showTargetBalanceSlider]="showTargetBalanceSlider()"/>
      </div>
    </app-filter-panel>
  `,
})
export class AccountFilterPanelComponent {

  accounts = input.required<Account[]>();

  form = viewChild.required(AccountFilterFormComponent);

  showTargetBalanceSlider = computed(() => this.accounts().some(acc => acc.balanceTarget !== undefined && acc.balanceTarget !== 0));

  protected readonly accountFilterStore = inject(AccountFilterStore);
}
