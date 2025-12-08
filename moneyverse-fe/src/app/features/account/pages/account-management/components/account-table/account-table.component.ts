import {Component, computed, effect, inject, input, output} from '@angular/core';
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
import {TranslationService} from '../../../../../../shared/services/translation.service';
import {ChipComponent} from '../../../../../../shared/components/chip/chip.component';
import {AccountStore} from '../../../../services/account.store';

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
    ChipComponent,
  ],
  templateUrl: './account-table.component.html',
})
export class AccountTableComponent {

  accounts = input.required<Account[]>();

  onDelete = output<Account>();
  onEdit = output<Account>();

  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  private readonly confirmationService = inject(AppConfirmationService);
  private readonly translateService = inject(TranslationService);

  config = computed<TableConfig<Account>>(() => {
    this.translateService.lang();
    return {
      currentPageReportTemplate: this.translateService.translate('app.table.pageReport', {
        first: '{first}',
        last: '{last}',
        totalRecords: '{totalRecords}'
      }),
      dataKey: 'accountId',
      paginator: true,
      rows: 5,
      rowsPerPageOptions: [5, 10, 25, 50],
      showCurrentPageReport: true,
      stripedRows: true,
      styleClass: 'mt-4'
    }
  });

  columns = computed<TableColumn<Account>[]>(() => {
    this.translateService.lang();
    return [
      {field: 'accountName', header: this.translateService.translate('app.name'), sortable: true},
      {field: 'accountDescription', header: this.translateService.translate('app.description')},
      {field: 'accountCategory', header: this.translateService.translate('app.category'), sortable: true},
      {field: 'currency', header: this.translateService.translate('app.currency'), sortable: true},
      {field: 'balance', header: this.translateService.translate('app.balance'), sortable: true},
      {field: 'balanceTarget', header: this.translateService.translate('app.balanceTarget'), sortable: true},
      {field: 'default', header: this.translateService.translate('app.default')}
    ]
  })

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
      message: this.translateService.translate('app.dialog.account.confirmDelete', {field: account.accountName}),
      header: this.translateService.translate('app.dialog.account.delete'),
      accept: () => this.onDelete.emit(account)
    });
  }

  protected readonly Number = Number;
}
