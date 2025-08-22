import {Component, computed, effect, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Category} from '../../../../category.model';
import {CategoryFormComponent} from '../category-form/category-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {CategoryFormData} from '../../models/form.model';

@Component({
  selector: 'app-category-form-dialog',
  imports: [
    ReactiveFormsModule,
    FormDialogComponent,
    CategoryFormComponent,
  ],
  template: `
    <app-form-dialog
      #formDialogRef
      [form]="form()"
      [config]="config()">
      <app-category-form [selectedItem]="formDialog().selectedItem()"/>
    </app-form-dialog>`
})
export class CategoryFormDialogComponent {

  onSubmit = output<CategoryFormData>();

  protected form = viewChild.required(CategoryFormComponent);
  protected formDialog = viewChild.required<FormDialogComponent<Category, CategoryFormData>>(FormDialogComponent<Category, CategoryFormData>);

  config = computed<DynamicDialogConfig>(() => ({
    header: this.formDialog().selectedItem() ? 'Edit Category' : 'Add Category',
    styleClass: 'w-[90vw] sm:w-[80vw] md:w-[60vw] lg:w-[40vw] lg:max-w-[550px]'
  }))

  constructor() {
    effect(() => this.form().onSubmit.subscribe(data => this.onSubmit.emit(data)));
  }

  open(item?: Category) {
    this.formDialog().open(item);
  }

}
