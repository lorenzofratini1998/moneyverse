import {Component, inject, signal, ViewChild} from '@angular/core';
import {Button} from "primeng/button";
import {SvgComponent} from "../../../../shared/components/svg/svg.component";
import {IconsEnum} from '../../../../shared/models/icons.model';
import {
  SubscriptionFormDialogComponent
} from './components/subscription-form-dialog/subscription-form-dialog.component';
import {Subscription, SubscriptionForm, SubscriptionFormData} from '../../transaction.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {switchMap, take} from 'rxjs';
import {SubscriptionFactory} from './models/subscription.factory';
import {TransactionService} from '../../transaction.service';
import {TransactionStore} from '../../transaction.store';
import {SubscriptionTableComponent} from './components/subscription-table/subscription-table.component';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-subscription-management',
  imports: [
    Button,
    SvgComponent,
    SubscriptionFormDialogComponent,
    SubscriptionTableComponent,
    Toast
  ],
  templateUrl: './subscription-management.component.html',
  styleUrl: './subscription-management.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class SubscriptionManagementComponent {

  @ViewChild(SubscriptionFormDialogComponent) subscriptionForm!: SubscriptionFormDialogComponent;

  protected readonly authService = inject(AuthService);
  protected readonly transactionService = inject(TransactionService);
  protected readonly transactionStore = inject(TransactionStore);
  private readonly messageService = inject(MessageService);

  protected readonly IconsEnum = IconsEnum;

  protected subscriptions = signal<Subscription[]>([]);

  constructor() {
    this.loadSubscriptions();
  }

  createSubscription(formData: SubscriptionFormData) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.transactionService.createSubscription(SubscriptionFactory.createSubscriptionRequest(userId, formData)))).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Subscription created successfully.'
        });
        this.loadSubscriptions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the subscription creation.'
        });
      }
    })
  }

  editSubscription(formData: SubscriptionForm) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.transactionService.updateSubscription(formData.subscriptionId!, SubscriptionFactory.createSubscriptionUpdateRequest(userId, formData.formData)))).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Subscription updated successfully.'
        });
        this.loadSubscriptions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the subscription update.'
        });
      }
    });
  }

  deleteSubscription(subscription: Subscription) {
    this.transactionService.deleteSubscription(subscription.subscriptionId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Subscription deleted successfully.'
        });
        this.loadSubscriptions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the subscription deletion.'
        });
      }
    });
  }

  loadSubscriptions() {
    const userId = this.authService.getAuthenticatedUser().userId;
    this.transactionService.getSubscriptionsByUser(userId).subscribe({
      next: (subscriptions) => this.subscriptions.set(subscriptions),
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Failed to load subscriptions.'
        });
      }
    });
  }
}
