import {Component, input, output} from '@angular/core';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyPipe, NgClass} from '@angular/common';
import {PercentagePipe} from '../../../../../shared/pipes/percentage.pipe';

@Component({
  selector: 'app-account-detail',
  templateUrl: './account-detail.component.html',
  imports: [
    NgClass,
    CurrencyPipe,
    PercentagePipe
  ]
})
export class AccountDetailComponent {
  account = input.required<Account>();
  category = input.required<AccountCategory>();
  close = output<void>();

  onClose(): void {
    this.close.emit();
  }
}
