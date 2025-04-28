import {Component, computed, effect, input, output} from '@angular/core';
import {Category} from '../../../budget.model';
import {CategoryTreeItemComponent} from '../category-tree-item/category-tree-item.component';

@Component({
  selector: 'app-category-tree',
  imports: [
    CategoryTreeItemComponent
  ],
  templateUrl: './category-tree.component.html',
  styleUrl: './category-tree.component.scss'
})
export class CategoryTreeComponent {
  categories = input.required<Category[]>();
  parentCategories = computed(() => this.categories().filter(category => category.parentCategory === undefined));
  deleteCategory = output<Category>()

  constructor() {
    effect(() => {
      console.log(this.categories())
      console.log(this.parentCategories())
    });
  }
}
