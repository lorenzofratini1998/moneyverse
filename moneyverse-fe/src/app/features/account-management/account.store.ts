import {Injectable, signal} from '@angular/core';
import {Account} from './account.model';

@Injectable({providedIn: 'root'})
export class AccountStore {
  private readonly selectedAccount$ = signal<Account | null>(null);
  private readonly isFormOpen$ = signal(false);

  selectedAccount = this.selectedAccount$.asReadonly();
  isFormOpen = this.isFormOpen$.asReadonly();

  openForm(account?: Account) {
    this.selectedAccount$.set(account ?? null);
    this.isFormOpen$.set(true);
  }

  closeForm() {
    this.selectedAccount$.set(null);
    this.isFormOpen$.set(false);
  }

  setSelectedAccount(account: Account) {
    this.selectedAccount$.set(account);
  }
}
