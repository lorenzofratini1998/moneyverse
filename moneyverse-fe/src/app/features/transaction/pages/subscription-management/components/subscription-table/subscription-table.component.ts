import {Component, computed, inject, input, output, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {RecurrenceRuleEnum, recurrenceRuleOptions, Subscription, Transaction} from '../../../../transaction.model';
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
  subscriptions = input.required<Subscription[]>();
  readonly = input<boolean>(false);
  expanded = input<boolean>(true);
  config = input<Partial<TableConfig<Subscription>>>({})
  onEdit = output<Subscription>();
  onDelete = output<Subscription>();

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly subscriptionTableService = inject(SubscriptionTableService);

  protected readonly Icons = IconsEnum;
  protected readonly math = Math;

  tableConfig = computed<TableConfig<Subscription>>(() => {
    const baseConfig: TableConfig<Subscription> = {
      stripedRows: true,
      paginator: true,
      rows: 5,
      rowsPerPageOptions: [5, 10, 25, 50],
      showCurrentPageReport: true,
      scrollable: true,
      currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
      dataKey: 'subscriptionId'
    };
    return {...baseConfig, ...this.config()}
  });

  columns = signal<TableColumn<Subscription>[]>([
    {field: "subscriptionName", header: "Name"},
    {field: "amount", header: "Amount", sortable: true},
    {field: "totalAmount", header: "Total Amount"},
    {field: "accountId", header: "Account"},
    {field: "categoryId", header: "Category"},
    {field: "recurrenceRule", header: "Recurrence"},
    {field: "startDate", header: "Start Date"},
    {field: "endDate", header: "End Date"},
    {field: "nextExecutionDate", header: "Next Payment Date", sortable: true},
    {field: "active", header: "Active"},
  ])

  actions = computed<TableAction<Subscription>[]>(() => [
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

  expandedConfig = computed<TableConfig<Transaction>>(() => ({
    dataKey: 'transactionId',
    paginator: true,
    rows: 5,
    showCurrentPageReport: true,
    currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
  }));

  expandedColumns = signal<TableColumn<Transaction>[]>([
    {field: "date", header: "Date"},
    {field: "description", header: "Description"},
    {field: "amount", header: "Amount"},
    {field: "normalizedAmount", header: "Normalized Amount"},
    {field: "accountId", header: "Account"},
    {field: "categoryId", header: "Category"},
  ])

  protected formatRecurrence(recurrence: string): string {
    return recurrenceRuleOptions.find(option => option.value === recurrence)?.label ?? '';
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

  private confirmDelete(subscription: Subscription) {
    this.subscriptionTableService.confirmDelete(subscription, () => this.onDelete.emit(subscription));
  }
}
