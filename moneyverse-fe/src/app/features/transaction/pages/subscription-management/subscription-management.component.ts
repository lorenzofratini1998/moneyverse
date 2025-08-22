import {Component, computed, inject, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {
  SubscriptionFormDialogComponent
} from './components/subscription-form-dialog/subscription-form-dialog.component';
import {Subscription} from '../../transaction.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {SubscriptionTableComponent} from './components/subscription-table/subscription-table.component';
import {SubscriptionStore} from './services/subscription.store';
import {SubscriptionFormData} from "./models/form.model";
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';

@Component({
  selector: 'app-subscription-management',
  imports: [
    SubscriptionFormDialogComponent,
    SubscriptionTableComponent,
    ManagementComponent
  ],
  templateUrl: './subscription-management.component.html'
})
export class SubscriptionManagementComponent {

  protected readonly subscriptionStore = inject(SubscriptionStore);
  private readonly authService = inject(AuthService);

  subscriptionFormDialog = viewChild.required(SubscriptionFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => (
    {
      title: 'Subscription Management',
      actions: [
        {
          icon: IconsEnum.REFRESH,
          variant: 'text',
          severity: 'secondary',
          action: () => this.subscriptionStore.loadSubscriptions(true)
        },
        {
          icon: IconsEnum.PLUS,
          label: 'New Subscription',
          action: () => this.subscriptionFormDialog().open()
        }
      ]
    }
  ))

  submit(formData: SubscriptionFormData) {
    const subscriptionId = formData.subscriptionId;
    if (subscriptionId) {
      this.subscriptionStore.updateSubscription({
        subscriptionId,
        request: {...formData}
      })
    } else {
      this.subscriptionStore.createSubscription({
          userId: this.authService.authenticatedUser.userId,
          ...formData
        }
      )
    }
  }

  deleteSubscription(subscription: Subscription) {
    this.subscriptionStore.deleteSubscription(subscription.subscriptionId);
  }
}
