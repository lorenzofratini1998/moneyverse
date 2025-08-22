import {Component, computed, input} from '@angular/core';
import {Category} from '../../../../category.model';
import {PrimeTemplate, TreeNode} from 'primeng/api';
import {OrganizationChart} from 'primeng/organizationchart';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';

@Component({
  selector: 'app-category-tree',
  imports: [
    OrganizationChart,
    PrimeTemplate,
    SvgComponent
  ],
  templateUrl: './category-tree.component.html'
})
export class CategoryTreeComponent {

  root = input.required<Category>();

  nodes = computed<TreeNode[]>(() => {
    const root = this.root();
    return root ? [this.buildTree(root)] : [];
  });

  private buildTree(category: Category): TreeNode<Category> {
    return {
      label: category.categoryName,
      expanded: true,
      data: category,
      children: category.children?.map(child => this.buildTree(child)) ?? []
    };
  }
}
