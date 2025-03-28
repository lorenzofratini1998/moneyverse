import {Component, input, output, ViewChild} from '@angular/core';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyPipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ConfirmDialogComponent} from '../../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {EyeIcon, LucideAngularModule, PencilIcon, StarIcon, Trash2Icon} from 'lucide-angular';

@Component({
  selector: 'app-account-card',
  templateUrl: './account-card.component.html',
  imports: [
    CurrencyPipe,
    FormsModule,
    ConfirmDialogComponent,
    LucideAngularModule
  ]
})
export class AccountCardComponent {

  protected readonly Trash2Icon = Trash2Icon;
  protected readonly PencilIcon = PencilIcon;
  protected readonly EyeIcon = EyeIcon;
  protected readonly StarIcon = StarIcon;

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
}
