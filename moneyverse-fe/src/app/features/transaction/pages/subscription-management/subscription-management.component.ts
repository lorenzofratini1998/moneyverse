import {Component, computed, inject, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {
  SubscriptionFormDialogComponent
} from './components/subscription-form-dialog/subscription-form-dialog.component';
import {SubscriptionTransaction} from '../../transaction.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {SubscriptionTableComponent} from './components/subscription-table/subscription-table.component';
import {SubscriptionStore} from './services/subscription.store';
import {SubscriptionFormData} from "./models/form.model";
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';
import {TranslationService} from '../../../../shared/services/translation.service';

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
  private readonly translateService = inject(TranslationService);

  subscriptionFormDialog = viewChild.required(SubscriptionFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => {
      this.translateService.lang();
      return {
        title: this.translateService.translate('app.manageSubscriptions'),
        actions: [
          {
            icon: IconsEnum.REFRESH,
            variant: 'text',
            severity: 'secondary',
            action: () => this.subscriptionStore.loadSubscriptions(true)
          },
          {
            icon: IconsEnum.PLUS,
            label: this.translateService.translate('app.actions.newSubscription'),
            action: () => this.subscriptionFormDialog().open()
          }
        ]
      }
    }
  )

  submit(formData: SubscriptionFormData) {
    const subscriptionId = formData.subscriptionId;
    if (subscriptionId) {
      this.subscriptionStore.updateSubscription({
        subscriptionId,
        request: {...formData}
      })
    } else {
      this.subscriptionStore.createSubscription({
          userId: this.authService.user().userId,
          ...formData
        }
      )
    }
  }

  deleteSubscription(subscription: SubscriptionTransaction) {
    this.subscriptionStore.deleteSubscription(subscription.subscriptionId);
  }
}
