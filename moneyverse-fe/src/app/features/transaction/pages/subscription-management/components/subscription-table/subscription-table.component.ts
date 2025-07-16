import {Component, inject, input, output, signal, ViewChild} from '@angular/core';
import {TableModule, TableRowExpandEvent} from 'primeng/table';
import {
  RecurrenceRuleEnum,
  recurrenceRuleOptions,
  Subscription,
  SubscriptionForm, SubscriptionFormData,
  Transaction
} from '../../../../transaction.model';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {DatePipe, NgClass} from '@angular/common';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../../account/account.store';
import {CategoryStore} from '../../../../../category/category.store';
import {Button, ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {TransactionService} from '../../../../transaction.service';
import {AuthService} from '../../../../../../core/auth/auth.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {CustomChipComponent} from '../../../../../../shared/components/custom-chip/custom-chip.component';
import {Tag} from 'primeng/tag';
import {SubscriptionFormDialogComponent} from '../subscription-form-dialog/subscription-form-dialog.component';
import {Toast} from 'primeng/toast';
import {ConfirmDialog} from 'primeng/confirmdialog';

@Component({
  selector: 'app-subscription-table',
  imports: [
    TableModule,
    CurrencyPipe,
    DatePipe,
    Button,
    Ripple,
    SvgComponent,
    ButtonDirective,
    CustomChipComponent,
    Tag,
    SubscriptionFormDialogComponent,
    NgClass,
    Toast,
    ConfirmDialog
  ],
  templateUrl: './subscription-table.component.html',
  styleUrl: './subscription-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class SubscriptionTableComponent {
  subscriptions = input.required<Subscription[]>();
  edited = output<SubscriptionForm>();
  deleted = output<Subscription>();

  @ViewChild(SubscriptionFormDialogComponent) subscriptionForm!: SubscriptionFormDialogComponent;

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly transactionService = inject(TransactionService);
  protected readonly authService = inject(AuthService);
  protected readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  protected readonly Icons = IconsEnum;
  protected subscriptionTransactions = signal<Transaction[]>([]);

  protected formatRecurrence(recurrence: string): string {
    return recurrenceRuleOptions.find(option => option.value === recurrence)?.label ?? '';
  }

  protected onRowExpand(event: TableRowExpandEvent) {
    this.authService.getAuthenticatedUser().userId;
    this.transactionService.getSubscription(event.data.subscriptionId).subscribe({
      next: (subscription) => this.subscriptionTransactions.set(subscription.transactions.sort((a, b) => b.date.getTime() - a.date.getTime())),
      error: () => {
        this.subscriptionTransactions.set([]);
        this.messageService.add({
          severity: 'error',
          detail: 'Failed to load subscription transactions.'
        });
      }
    });
  }

  protected onRowCollapse(event: TableRowExpandEvent) {
    this.subscriptionTransactions.set([]);
  }

  protected getSeverity(status: string) {
    switch (status) {
      case RecurrenceRuleEnum.YEARLY:
        return 'warning';
      case RecurrenceRuleEnum.MONTHLY:
        return 'success';
      default:
        return 'info';
    }
  }

  onEdit(formData: SubscriptionFormData) {
    this.edited.emit({
      subscriptionId: this.subscriptionForm.subscriptionToEdit()?.subscriptionId,
      formData: formData
    });
  }

  onDelete(event: Event, subscription: Subscription) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure you want to delete this subscription? All associated transactions will be deleted.',
      header: 'Delete subscription',
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
        this.deleted.emit(subscription);
      },
    })
  }
}
