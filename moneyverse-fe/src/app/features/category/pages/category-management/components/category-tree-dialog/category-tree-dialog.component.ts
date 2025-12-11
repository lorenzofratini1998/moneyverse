import {Component, computed, viewChild} from '@angular/core';
import {Category} from '../../../../category.model';
import {DialogComponent} from '../../../../../../shared/components/dialogs/dialog/dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {CategoryTreeComponent} from '../category-tree/category-tree.component';

@Component({
  selector: 'app-category-tree-dialog',
  imports: [
    DialogComponent,
    CategoryTreeComponent
  ],
  template: `
    <app-dialog [config]="config()">
      <div class="card flex justify-center" content>
        @if (dialog().selectedItem(); as selectedItem) {
          <app-category-tree [root]="selectedItem"/>
        }
      </div>
    </app-dialog>
  `
})
export class CategoryTreeDialogComponent {
  protected dialog = viewChild.required<DialogComponent<Category>>(DialogComponent<Category>);

  config = computed<DynamicDialogConfig>(() => ({
    header: 'Category Tree',
    styleClass: 'w-11/12 md:w-1/2 lg:w-1/3'
  }));

  open(item?: Category) {
    this.dialog().open(item);
  }
}
