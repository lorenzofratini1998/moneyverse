import {Component, inject, input, output, ViewChild} from '@angular/core';
import {Category} from '../../../budget.model';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';
import {CategoryBadgeComponent} from '../category-badge/category-badge.component';
import {CategoryStore} from '../../../category.store';
import {ConfirmDialogComponent} from '../../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-category-tree-item',
  imports: [
    SvgComponent,
    CategoryBadgeComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './category-tree-item.component.html',
  styleUrl: './category-tree-item.component.scss'
})
export class CategoryTreeItemComponent {
  category = input.required<Category>();
  level = input<number>(0);
  protected categoryStore = inject(CategoryStore);
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  delete = output<Category>();

  protected readonly IconsEnum = IconsEnum;
  protected readonly Icons = IconsEnum;

  openConfirmDialog(): void {
    this.confirmDialog.show();
    this.confirmDialog.confirm.subscribe(result => {
      if (result) {
        this.delete.emit(this.category());
      }
    });
  }
}
