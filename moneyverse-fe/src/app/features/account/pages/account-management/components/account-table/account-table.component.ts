import {Component, computed, inject, input, output, signal} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {Account} from '../../../../account.model';
import {FormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';
import {Tag} from 'primeng/tag';
import {TableAction, TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {TableActionsComponent} from '../../../../../../shared/components/table-actions/table-actions.component';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';

@Component({
  selector: 'app-account-table',
  imports: [
    SvgComponent,
    FormsModule,
    TableModule,
    Tag,
    TableComponent,
    CurrencyPipe,
    CellTemplateDirective,
    TableActionsComponent,
  ],
  templateUrl: './account-table.component.html',
})
export class AccountTableComponent {

  accounts = input.required<Account[]>();

  onDelete = output<Account>();
  onEdit = output<Account>();

  protected readonly Icons = IconsEnum;
  private readonly confirmationService = inject(AppConfirmationService);

  config = computed<TableConfig<Account>>(() => ({
    currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
    dataKey: 'accountId',
    paginator: true,
    rows: 5,
    rowsPerPageOptions: [5, 10, 25, 50],
    showCurrentPageReport: true,
    stripedRows: true,
    styleClass: 'mt-4'
  }));

  columns = signal<TableColumn<Account>[]>([
    {field: 'accountName', header: 'Name', sortable: true},
    {field: 'accountDescription', header: 'Description'},
    {field: 'accountCategory', header: 'Category', sortable: true},
    {field: 'currency', header: 'Currency', sortable: true},
    {field: 'balance', header: 'Balance', sortable: true},
    {field: 'balanceTarget', header: 'Balance Target', sortable: true},
    {field: 'default', header: 'Default'}
  ])

  actions = computed<TableAction<Account>[]>(() => [
    {
      icon: IconsEnum.PENCIL,
      severity: 'secondary',
      click: (account) => this.onEdit.emit(account),
    },
    {
      icon: IconsEnum.TRASH,
      severity: 'danger',
      click: (account) => this.confirmDelete(account),
    }
  ]);

  private confirmDelete(account: Account) {
    this.confirmationService.confirmDelete({
      message: `Are you sure you want to delete the account "${account.accountName}"? All associated transactions will be deleted.`,
      header: 'Delete Account',
      accept: () => this.onDelete.emit(account)
    });
  }
}
