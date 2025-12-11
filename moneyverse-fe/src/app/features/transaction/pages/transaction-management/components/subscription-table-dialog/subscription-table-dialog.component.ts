import {Component, computed, viewChild} from '@angular/core';
import {DialogComponent} from '../../../../../../shared/components/dialogs/dialog/dialog.component';
import {SubscriptionTransaction} from '../../../../transaction.model';
import {
  SubscriptionTableComponent
} from '../../../subscription-management/components/subscription-table/subscription-table.component';
import {TableConfig} from '../../../../../../shared/models/table.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-subscription-table-dialog',
  imports: [
    DialogComponent,
    SubscriptionTableComponent,
    SvgComponent
  ],
  template: `
    <app-dialog [config]="config()">
      <div header>
        <h3 class="flex items-center gap-2">
          <app-svg name="calendar-sync" class="size-6"/>
          <span>{{ config().header ?? '' }}</span>
        </h3>
      </div>
      @if (dialog().selectedItem(); as selectedItem) {
        <div content>
          <app-subscription-table [subscriptions]="[selectedItem]"
                                  [readonly]="true"
                                  [expanded]="false"
                                  [config]="tableConfig()"/>
        </div>
      }
    </app-dialog>
  `
})
export class SubscriptionTableDialogComponent {
  protected dialog = viewChild.required<DialogComponent<SubscriptionTransaction>>(DialogComponent<SubscriptionTransaction>)

  config = computed(() => ({
    header: this.dialog().selectedItem()?.subscriptionName,
    styleClass: 'w-[90vw] lg:w-[65vw] xl:w-[50vw] lg:max-w-[700px]'
  }))

  tableConfig = computed<TableConfig<SubscriptionTransaction>>(() => ({
    paginator: false,
    scrollable: true,
    showCurrentPageReport: false,
    tableStyle: {'min-width': '60rem'}
  }))

  open(item?: SubscriptionTransaction) {
    this.dialog().open(item);
  }
}
