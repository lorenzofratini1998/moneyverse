import {Component, computed, effect, input, viewChild} from '@angular/core';
import {CancelButtonComponent} from '../../forms/cancel-button/cancel-button.component';
import {SubmitButtonComponent} from '../../forms/submit-button/submit-button.component';
import {AbstractFormComponent} from '../../forms/abstract-form.component';
import {DialogComponent} from '../dialog/dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';

@Component({
  selector: 'app-form-dialog',
  imports: [
    CancelButtonComponent,
    SubmitButtonComponent,
    DialogComponent
  ],
  templateUrl: './form-dialog.component.html'
})
export class FormDialogComponent<T, D> {

  config = input.required<DynamicDialogConfig>();
  form = input.required<AbstractFormComponent<T, D>>();

  dialog = viewChild.required(DialogComponent<T>);

  selectedItem = computed(() => this.dialog().selectedItem());

  constructor() {
    effect(() => {
      const form = this.form();
      if (form) {
        form.onSubmit.subscribe(() => this.close());
      }
    });

    effect(() => {
      const form = this.form();
      const selectedItem = this.selectedItem();
      if (form) {
        if (selectedItem) {
          this.form().patch(selectedItem);
        } else {
          this.form().reset();
        }
      }
    });
  }

  open(item?: T) {
    this.dialog().open(item);
  }

  close() {
    this.dialog().close();
  }

}
