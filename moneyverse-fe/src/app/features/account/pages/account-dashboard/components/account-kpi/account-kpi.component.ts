import {Component, inject} from '@angular/core';
import {CurrencyPipe} from "@angular/common";
import {AccountStore} from '../../../../account.store';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {PreferenceKey} from '../../../../../../shared/models/preference.model';

@Component({
  selector: 'app-account-kpi',
  imports: [
    CurrencyPipe
  ],
  templateUrl: './account-kpi.component.html',
  styleUrl: './account-kpi.component.scss'
})
export class AccountKpiComponent {

  protected readonly Icons = IconsEnum;
  protected readonly PreferenceKey = PreferenceKey;
  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);

  getTotalBalance(): number {
    return this.accountStore.accounts().reduce((total, account) => total + account.balance, 0);
  }
}
