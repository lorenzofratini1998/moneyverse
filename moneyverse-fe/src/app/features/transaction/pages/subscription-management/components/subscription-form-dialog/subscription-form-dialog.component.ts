import {Component, computed, effect, inject, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {SubscriptionTransaction} from '../../../../transaction.model';
import {SubscriptionFormComponent} from '../subscription-form/subscription-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {SubscriptionFormData} from "../../models/form.model";
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-subscription-form-dialog',
  imports: [
    ReactiveFormsModule,
    FormDialogComponent,
    SubscriptionFormComponent
  ],
  template: `
    <app-form-dialog [form]="form()" [config]="config()">
      <app-subscription-form [selectedItem]="formDialog().selectedItem()"/>
    </app-form-dialog>
  `
})
export class SubscriptionFormDialogComponent {
  onSubmit = output<SubscriptionFormData>();

  protected form = viewChild.required(SubscriptionFormComponent);
  protected formDialog = viewChild.required(FormDialogComponent<SubscriptionTransaction, SubscriptionFormData>);

  private readonly translateService = inject(TranslationService);

  config = computed<DynamicDialogConfig>(() => {
    this.translateService.lang();
    return {
      header: this.formDialog().selectedItem() ? this.translateService.translate('app.dialog.subscription.edit') : this.translateService.translate('app.dialog.subscription.add'),
        styleClass: 'w-[90vw] lg:w-[65vw] xl:w-[50vw] lg:max-w-[700px]'
    }
  })

  constructor() {
    effect(() => this.form().onSubmit.subscribe(data => this.onSubmit.emit(data)));
  }

  open(item?: SubscriptionTransaction) {
    this.formDialog().open(item);
  }

}
