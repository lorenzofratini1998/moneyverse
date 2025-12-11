import {Component, computed, effect, inject, output, viewChild} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {Tag} from '../../../../transaction.model';
import {TagFormComponent} from '../tag-form/tag-form.component';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {TagFormData} from "../../models/form.model";
import {TranslationService} from '../../../../../../shared/services/translation.service';

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
  private readonly translateService = inject(TranslationService);

  config = computed<DynamicDialogConfig>(() => {
    this.translateService.lang();
    return {
      header: this.formDialog().selectedItem() ? this.translateService.translate('app.dialog.tag.edit') : this.translateService.translate('app.dialog.tag.add'),
      styleClass: 'w-[90vw] sm:w-[80vw] md:w-[60vw] lg:w-[40vw] lg:max-w-[550px]'
    }
  })

  constructor() {
    effect(() => this.form().onSubmit.subscribe(data => this.onSubmit.emit(data)));
  }

  open(item?: Tag) {
    this.formDialog().open(item);
  }
}
