import {Injectable, signal} from '@angular/core';
import {Transaction} from './transaction.model';

@Injectable({providedIn: 'root'})
export class TransactionStore {
  private readonly selectedTransaction$ = signal<Transaction | null>(null);
  private readonly isFormOpen$ = signal(false);

  selectedTransaction = this.selectedTransaction$.asReadonly();
  isFormOpen = this.isFormOpen$.asReadonly();

  openForm(transaction?: Transaction) {
    this.selectedTransaction$.set(transaction ?? null);
    this.isFormOpen$.set(true);
  }

  closeForm() {
    this.selectedTransaction$.set(null);
    this.isFormOpen$.set(false);
  }
}
