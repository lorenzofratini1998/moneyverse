import {Component, viewChild} from '@angular/core';
import {
  CategoryAnalyticsTransactionsTableComponent
} from '../category-analytics-transactions-table/category-analytics-transactions-table.component';
import {Category} from '../../../../category.model';
import {
  AnalyticsTransactionDialogComponent
} from '../../../../../analytics/components/analytics-transaction-dialog/analytics-transaction-dialog.component';

@Component({
  selector: 'app-category-analytics-transaction-table-dialog',
  imports: [
    AnalyticsTransactionDialogComponent,
    CategoryAnalyticsTransactionsTableComponent,
  ],
  template: `
    <app-analytics-transaction-dialog
      [tableTemplate]="categoryTableTemplate">

      <ng-template #categoryTableTemplate let-item>
        <app-category-analytics-transactions-table [data]="item"/>
      </ng-template>
    </app-analytics-transaction-dialog>
  `
})
export class CategoryAnalyticsTransactionTableDialogComponent {
  dialog = viewChild.required<AnalyticsTransactionDialogComponent<Category>>(AnalyticsTransactionDialogComponent<Category>);

  open(item?: Category[]) {
    this.dialog().open(item);
  }
}
