import {Component, computed, inject, signal} from '@angular/core';
import {Transfer} from '../../../../transaction.model';
import {TableModule} from 'primeng/table';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {Account} from '../../../../../account/account.model';
import {AccountStore} from '../../../../../account/account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {Dialog} from 'primeng/dialog';

@Component({
  selector: 'app-transfer-table-dialog',
  imports: [
    TableModule,
    CurrencyPipe,
    Dialog
  ],
  templateUrl: './transfer-table-dialog.component.html',
  styleUrl: './transfer-table-dialog.component.scss'
})
export class TransferTableDialogComponent {

  protected readonly preferenceStore = inject(PreferenceStore);
  private readonly accountStore = inject(AccountStore);

  protected _isOpen = false
  protected transfer = signal<Transfer | null>(null);

  protected transactions = computed(() => {
    const _transfer = this.transfer();
    if (!_transfer) {
      return [];
    }
    return [_transfer.transactionFrom, _transfer.transactionTo];
  })

  accountsMap = computed<Map<string, Account>>(() => {
    return new Map(this.accountStore.accounts().map(account => [account.accountId, account]));
  })

  open(transfer: Transfer) {
    this.transfer.set(transfer);
    this._isOpen = true;
  }

  close() {
    this._isOpen = false;
    this.transfer.set(null);
  }
}
