import {Component, computed, effect, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Account} from '../../../../account.model';
import {AccountFormComponent} from '../account-form/account-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {AccountFormData} from '../../models/form.model';

@Component({
  selector: 'app-account-form-dialog',
  imports: [
    ReactiveFormsModule,
    FormDialogComponent,
    AccountFormComponent
  ],
  template: `
    <app-form-dialog
      [form]="form()"
      [config]="config()">
      <app-account-form [selectedItem]="formDialog().selectedItem()"/>
    </app-form-dialog>
  `
})
export class AccountFormDialogComponent {

  onSubmit = output<AccountFormData>();

  protected form = viewChild.required(AccountFormComponent);
  protected formDialog = viewChild.required(FormDialogComponent<Account, AccountFormData>);

  config = computed<DynamicDialogConfig>(() => ({
    header: this.formDialog().selectedItem() ? 'Edit Account' : 'Add Account',
    styleClass: 'w-[90vw] sm:w-[80vw] md:w-[60vw] lg:w-[40vw] lg:max-w-[550px]'
  }));

  constructor() {
    effect(() => {
      this.form().onSubmit.subscribe(data => this.onSubmit.emit(data));
    })
  }

  open(item?: Account) {
    this.formDialog().open(item);
  }

}
