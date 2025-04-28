import {Component, input, output, ViewChild} from '@angular/core';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyPipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ConfirmDialogComponent} from '../../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {LucideAngularModule} from 'lucide-angular';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  imports: [
    CurrencyPipe,
    FormsModule,
    ConfirmDialogComponent,
    LucideAngularModule,
    SvgComponent
  ]
})
export class AccountCardComponent {

  account = input.required<Account>();
  category = input.required<AccountCategory>();
  view = output<Account>();
  edit = output<Account>();
  delete = output<Account>();
  @ViewChild('confirmDialog') confirmDialog!: ConfirmDialogComponent;

  onView(): void {
    this.view.emit(this.account());
  }

  onEdit(): void {
    this.edit.emit(this.account());
  }

  openConfirmDialog(): void {
    this.confirmDialog.show();
    this.confirmDialog.confirm.subscribe(result => {
      if (result) {
        this.delete.emit(this.account());
      }
    });
  }

  protected readonly Icons = IconsEnum;
}
