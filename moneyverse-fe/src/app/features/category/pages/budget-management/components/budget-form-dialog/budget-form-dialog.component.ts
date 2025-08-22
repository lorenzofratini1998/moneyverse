import {Component, computed, effect, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Budget} from '../../../../category.model';
import {BudgetFormComponent} from '../budget-form/budget-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {BudgetFormData} from '../../models/form.models';

@Component({
  selector: 'app-budget-form-dialog',
  imports: [
    ReactiveFormsModule,
    FormDialogComponent,
    BudgetFormComponent,
  ],
  template: `
    <app-form-dialog
      [form]="form()"
      [config]="config()">
      <app-budget-form [selectedItem]="formDialog().selectedItem()"/>
    </app-form-dialog>
  `
})
export class BudgetFormDialogComponent {

  onSubmit = output<BudgetFormData>();

  protected form = viewChild.required(BudgetFormComponent);
  protected formDialog = viewChild.required(FormDialogComponent<Budget, BudgetFormData>);

  config = computed<DynamicDialogConfig>(() => ({
    header: this.formDialog().selectedItem() ? 'Edit Budget' : 'Add Budget',
    styleClass: 'w-[80vw] sm:w-[70vw] md:w-[50vw] lg:w-[40vw] lg:max-w-[550px]'
  }))

  constructor() {
    effect(() => {
      this.form().onSubmit.subscribe(data => this.onSubmit.emit(data));
    })
  }

  open(item?: Budget) {
    this.formDialog().open(item);
  }

}
