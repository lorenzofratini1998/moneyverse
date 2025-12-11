import {Component, computed, effect, inject, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Account} from '../../../../account.model';
import {AccountFormComponent} from '../account-form/account-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {AccountFormData} from '../../models/form.model';
import {TranslationService} from '../../../../../../shared/services/translation.service';

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
  private readonly translateService = inject(TranslationService);

  protected form = viewChild.required(AccountFormComponent);
  protected formDialog = viewChild.required(FormDialogComponent<Account, AccountFormData>);

  config = computed<DynamicDialogConfig>(() => {
    this.translateService.lang();
    return {
      header: this.formDialog().selectedItem() ? this.translateService.translate('app.dialog.account.edit') : this.translateService.translate('app.dialog.account.add'),
      styleClass: 'w-[90vw] sm:w-[80vw] md:w-[60vw] lg:w-[40vw] lg:max-w-[550px]'
    }
  });

  constructor() {
    effect(() => {
      this.form().onSubmit.subscribe(data => this.onSubmit.emit(data));
    })
  }

  open(item?: Account) {
    this.formDialog().open(item);
  }

}
