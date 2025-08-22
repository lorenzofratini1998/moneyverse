import {Component, computed, effect, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Tag} from '../../../../transaction.model';
import {TagFormComponent} from '../tag-form/tag-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {TagFormData} from "../../models/form.model";

@Component({
  selector: 'app-tag-form-dialog',
  imports: [
    ReactiveFormsModule,
    FormDialogComponent,
    TagFormComponent
  ],
  template: `
    <app-form-dialog [form]="form()" [config]="config()">
      <app-tag-form [selectedItem]="formDialog().selectedItem()"/>
    </app-form-dialog>
  `
})
export class TagFormDialogComponent {

  onSubmit = output<TagFormData>();

  protected form = viewChild.required(TagFormComponent);
  protected formDialog = viewChild.required(FormDialogComponent<Tag, TagFormData>);

  config = computed<DynamicDialogConfig>(() => ({
    header: this.formDialog().selectedItem() ? 'Edit Tag' : 'Add Tag',
    styleClass: 'w-[90vw] sm:w-[80vw] md:w-[60vw] lg:w-[40vw] lg:max-w-[550px]'
  }))

  constructor() {
    effect(() => this.form().onSubmit.subscribe(data => this.onSubmit.emit(data)));
  }

  open(item?: Tag) {
    this.formDialog().open(item);
  }
}
