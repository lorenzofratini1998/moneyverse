import {Component, effect, inject, input, signal} from '@angular/core';
import {Category} from '../../../../category.model';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Dialog} from 'primeng/dialog';
import {CategoryStore} from '../../../../category.store';
import {PrimeTemplate, TreeNode} from 'primeng/api';
import {OrganizationChart} from 'primeng/organizationchart';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-category-tree-dialog',
  imports: [
    Dialog,
    OrganizationChart,
    PrimeTemplate,
    SvgComponent
  ],
  templateUrl: './category-tree-dialog.component.html',
  styleUrl: './category-tree-dialog.component.scss'
})
export class CategoryTreeDialogComponent {
  protected readonly IconsEnum = IconsEnum;
  protected readonly categoryStore = inject(CategoryStore);
  isOpen = input<boolean>(false);
  protected _isOpen = false;

  protected treeNodes = signal<TreeNode[]>([]);

  constructor() {
    effect(() => {
      if (this.isOpen() !== this._isOpen) {
        this._isOpen = this.isOpen();
      }
    });
  }

  open(category: Category) {
    this._isOpen = true;
    this.treeNodes.set([this.buildTree(category)]);
  }

  private buildTree(category: Category): TreeNode<Category> {
    return {
      label: category.categoryName,
      expanded: true,
      data: category,
      children: category.children?.map(child => this.buildTree(child)) ?? []
    };
  }

  close() {
    this._isOpen = false;
  }
}
