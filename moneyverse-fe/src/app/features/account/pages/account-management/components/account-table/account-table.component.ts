import {Component, computed, inject, output, signal} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {CurrencyPipe, NgClass} from '@angular/common';
import {AccountStore} from '../../../../account.store';
import {Account} from '../../../../account.model';
import {FormsModule} from '@angular/forms';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {Table, TableModule} from 'primeng/table';
import {InputText} from 'primeng/inputtext';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {Select} from 'primeng/select';
import {Tag} from 'primeng/tag';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {InputNumber} from 'primeng/inputnumber';
import {MultiSelect} from 'primeng/multiselect';

@Component({
  selector: 'app-account-table',
  imports: [
    SvgComponent,
    NgClass,
    CurrencyPipe,
    FormsModule,
    ConfirmDialog,
    TableModule,
    InputText,
    ButtonDirective,
    Ripple,
    Select,
    Tag,
    InputNumber,
    CurrencyPipe,
    MultiSelect
  ],
  templateUrl: './account-table.component.html',
  styleUrl: './account-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class AccountTableComponent {
  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  protected readonly currencyStore = inject(CurrencyStore);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);
  delete = output<Account>();
  rowEditSave = output<Account>();

  searchValue: string | undefined;

  currentPage = signal(1);
  itemsPerPage = signal(10);

  sortColumn = signal<string | null>(null);
  sortDirection = signal<'asc' | 'desc'>('asc');

  clear(table: Table) {
    table.clear();
    this.searchValue = ''
  }

  paginatedAccounts = computed(() => {
    const page = this.currentPage();
    const itemsPerPage = this.itemsPerPage();
    const column = this.sortColumn();
    const direction = this.sortDirection();

    let sortedAccounts = [...this.accountStore.filteredAccounts()];

    if (column) {
      sortedAccounts.sort((a, b) => {
        let valueA = a[column as keyof Account];
        let valueB = b[column as keyof Account];

        if (column === 'balance' || column === 'balanceTarget') {
          valueA = valueA || 0;
          valueB = valueB || 0;
        }

        if (column === 'default') {
          valueA = valueA ? 1 : 0;
          valueB = valueB ? 1 : 0;
        }

        if (typeof valueA === 'string' && typeof valueB === 'string') {
          return direction === 'asc'
            ? valueA.localeCompare(valueB)
            : valueB.localeCompare(valueA);
        } else {
          return direction === 'asc'
            ? (valueA as number) - (valueB as number)
            : (valueB as number) - (valueA as number);
        }
      });
    }

    return sortedAccounts.slice((page - 1) * itemsPerPage, page * itemsPerPage);
  });

  sortData(column: string): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    }
    this.currentPage.set(1);
  }

  handlePageChange(page: number) {
    this.currentPage.set(page);
  }

  handleItemsPerPageChange(size: number) {
    this.itemsPerPage.set(size);
    this.currentPage.set(1);
  }

  deleteAccount(account: Account): void {
    this.delete.emit(account);
  }

  onDeleteAccount(event: Event, account: Account) {
    /*this.confirmDialog.show();
    this.confirmDialog.confirm.subscribe(result => {
      if (result) {
        this.deleteAccount(account);
      }
    });*/
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure you want to delete this account? All associated transactions will be deleted.',
      header: 'Delete account',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
      },
      accept: () => {
        this.deleteAccount(account);
      },
    })
  }

  onRowEditSave(account: Account) {
    this.rowEditSave.emit(account);
    console.log(account);
  }
}
