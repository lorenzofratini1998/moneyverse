import {Component, inject, output, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {CurrencyPipe, NgClass} from '@angular/common';
import {AccountStore} from '../../../../account.store';
import {Account, AccountForm, AccountFormData} from '../../../../account.model';
import {FormsModule} from '@angular/forms';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {ButtonDirective} from 'primeng/button';
import {Tag} from 'primeng/tag';
import {AccountFormDialogComponent} from '../account-form-dialog/account-form-dialog.component';

@Component({
  selector: 'app-account-table',
  imports: [
    SvgComponent,
    NgClass,
    CurrencyPipe,
    FormsModule,
    ConfirmDialog,
    TableModule,
    ButtonDirective,
    Tag,
    CurrencyPipe,
    AccountFormDialogComponent
  ],
  templateUrl: './account-table.component.html',
  styleUrl: './account-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class AccountTableComponent {
  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  private readonly confirmationService = inject(ConfirmationService);

  deleted = output<Account>();
  edited = output<AccountForm>();

  @ViewChild(AccountFormDialogComponent) accountForm!: AccountFormDialogComponent;

  onDelete(event: Event, account: Account) {
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

  private deleteAccount(account: Account): void {
    this.deleted.emit(account);
  }

  onEdit(formData: AccountFormData) {
    this.edited.emit({
      accountId: this.accountForm.accountToEdit()?.accountId,
      formData: formData
    });
  }
}
