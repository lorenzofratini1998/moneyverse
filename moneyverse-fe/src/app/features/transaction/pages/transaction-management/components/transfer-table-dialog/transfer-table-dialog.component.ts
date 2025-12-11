import {Component, computed, inject, linkedSignal, viewChild} from '@angular/core';
import {Transfer} from '../../../../transaction.model';
import {TableModule} from 'primeng/table';
import {DialogComponent} from '../../../../../../shared/components/dialogs/dialog/dialog.component';
import {TransferTableComponent} from '../transfer-table/transfer-table.component';
import {AccountStore} from '../../../../../account/services/account.store';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-transfer-table-dialog',
  imports: [
    TableModule,
    DialogComponent,
    TransferTableComponent,
    SvgComponent
  ],
  template: `
    <app-dialog [config]="config()">
      <div header>
        <h3 class="flex items-center gap-2">
          <app-svg name="arrow-left-right" class="size-6"/>
          <span>{{ config().header ?? '' }}</span>
        </h3>
      </div>
      @if (dialog().selectedItem(); as selectedItem) {
        <div content>
          <app-transfer-table [transfer]="selectedItem"/>
        </div>
      }
    </app-dialog>
  `,
})
export class TransferTableDialogComponent {

  protected dialog = viewChild.required<DialogComponent<Transfer>>(DialogComponent<Transfer>);

  private readonly accountStore = inject(AccountStore);
  private readonly translateService = inject(TranslationService);

  private accountFrom = computed(() => this.accountStore.accountsMap().get(this.dialog().selectedItem()?.transactionFrom.accountId!)?.accountName);
  private accountTo = computed(() => this.accountStore.accountsMap().get(this.dialog().selectedItem()?.transactionTo.accountId!)?.accountName);

  config = linkedSignal(() => {
    this.translateService.lang();
    return {
      header: this.translateService.translate('app.dialog.transfer.detail', {from: this.accountFrom(), to: this.accountTo()}),
    };
  });

  open(item?: Transfer) {
    this.dialog().open(item);
  }
}
