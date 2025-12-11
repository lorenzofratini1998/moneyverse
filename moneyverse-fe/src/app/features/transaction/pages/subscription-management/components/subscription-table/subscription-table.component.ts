import {Component, computed, inject, input, output, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {
  RecurrenceRuleEnum,
  recurrenceRuleOptions,
  SubscriptionTransaction,
  Transaction
} from '../../../../transaction.model';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {DatePipe} from '@angular/common';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../../account/services/account.store';
import {CategoryStore} from '../../../../../category/services/category.store';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {ChipComponent} from '../../../../../../shared/components/chip/chip.component';
import {Tag} from 'primeng/tag';
import {TableAction, TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {TableActionsComponent} from '../../../../../../shared/components/table-actions/table-actions.component';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';
import {SubscriptionTableService} from './subscription-table.service';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-subscription-table',
  imports: [
    TableModule,
    CurrencyPipe,
    DatePipe,
    SvgComponent,
    ChipComponent,
    Tag,
    TableComponent,
    CellTemplateDirective,
    TableActionsComponent
  ],
  templateUrl: './subscription-table.component.html'
})
export class SubscriptionTableComponent {
  subscriptions = input.required<SubscriptionTransaction[]>();
  readonly = input<boolean>(false);
  expanded = input<boolean>(true);
  config = input<Partial<TableConfig<SubscriptionTransaction>>>({})
  onEdit = output<SubscriptionTransaction>();
  onDelete = output<SubscriptionTransaction>();

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly subscriptionTableService = inject(SubscriptionTableService);
  private readonly translateService = inject(TranslationService);

  protected readonly Icons = IconsEnum;
  protected readonly math = Math;

  tableConfig = computed<TableConfig<SubscriptionTransaction>>(() => {
    this.translateService.lang();
    const baseConfig: TableConfig<SubscriptionTransaction> = {
      stripedRows: true,
      paginator: true,
      rows: 5,
      rowsPerPageOptions: [5, 10, 25, 50],
      showCurrentPageReport: true,
      scrollable: true,
      currentPageReportTemplate: this.translateService.translate('app.table.pageReport', {
        first: '{first}',
        last: '{last}',
        totalRecords: '{totalRecords}'
      }),
      dataKey: 'subscriptionId'
    };
    return {...baseConfig, ...this.config()}
  });

  columns = computed<TableColumn<SubscriptionTransaction>[]>(() => {
    this.translateService.lang();
    return [
      {field: "subscriptionName", header: this.translateService.translate('app.name')},
      {field: "amount", header: this.translateService.translate('app.amount'), sortable: true},
      {field: "totalAmount", header: this.translateService.translate('app.form.totalAmount')},
      {field: "accountId", header: this.translateService.translate('app.account')},
      {field: "categoryId", header: this.translateService.translate('app.category')},
      {field: "recurrenceRule", header: this.translateService.translate('app.form.recurrence')},
      {field: "startDate", header: this.translateService.translate('app.form.dateStart')},
      {field: "endDate", header: this.translateService.translate('app.form.dateEnd')},
      {field: "nextExecutionDate", header: this.translateService.translate('app.form.nextPayment'), sortable: true},
      {field: "active", header: this.translateService.translate('app.form.active')},
    ]
  })

  actions = computed<TableAction<SubscriptionTransaction>[]>(() => [
    {
      icon: IconsEnum.PENCIL,
      severity: "secondary",
      click: (subscription) => this.onEdit.emit(subscription),
    },
    {
      icon: IconsEnum.TRASH,
      severity: "danger",
      click: (subscription) => this.confirmDelete(subscription),
    }
  ]);

  expandedConfig = computed<TableConfig<Transaction>>(() => {
    this.translateService.lang();
    return {
      dataKey: 'transactionId',
      paginator: true,
      rows: 5,
      showCurrentPageReport: true,
      currentPageReportTemplate: this.translateService.translate('app.table.pageReport', {
        first: '{first}',
        last: '{last}',
        totalRecords: '{totalRecords}'
      }),
    }
  });

  expandedColumns = computed<TableColumn<Transaction>[]>(() => {
    this.translateService.lang();
    return [
      {field: "date", header: this.translateService.translate('app.date')},
      {field: "description", header: this.translateService.translate('app.description')},
      {field: "amount", header: this.translateService.translate('app.amount')},
      {field: "normalizedAmount", header: this.translateService.translate('app.normalizedAmount')},
      {field: "accountId", header: this.translateService.translate('app.account')},
      {field: "categoryId", header: this.translateService.translate('app.category')},
    ]
  })

  protected formatRecurrence(recurrence: string): string {
    const option = recurrenceRuleOptions.find(o => o.value === recurrence);
    return option ? this.translateService.translate(option.label) : '';
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

  private confirmDelete(subscription: SubscriptionTransaction) {
    this.subscriptionTableService.confirmDelete(subscription, () => this.onDelete.emit(subscription));
  }
}
