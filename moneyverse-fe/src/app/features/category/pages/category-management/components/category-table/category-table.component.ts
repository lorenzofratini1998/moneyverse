import {Component, computed, inject, output, signal} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {CategoryStore} from '../../../../category.store';
import {Category} from '../../../../category.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {
  CategoryBadgeComponent
} from '../category-badge/category-badge.component';
import {DialogComponent} from '../../../../../../shared/components/dialog/dialog.component';
import {CategoryTreeComponent} from '../category-tree/category-tree.component';
import {PaginationComponent} from '../../../../../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-category-table',
  imports: [
    SvgComponent,
    CategoryBadgeComponent,
    DialogComponent,
    CategoryTreeComponent,
    PaginationComponent
  ],
  templateUrl: './category-table.component.html',
  styleUrl: './category-table.component.scss'
})
export class CategoryTableComponent {
  protected readonly Icons = IconsEnum;
  protected readonly categoryStore = inject(CategoryStore);

  currentPage = signal(1);
  itemsPerPage = signal(10);
  delete = output<Category>()

  paginatedCategories = computed(() => {
    const start = (this.currentPage() - 1) * this.itemsPerPage();
    const end = start + this.itemsPerPage();
    return this.categoryStore.categories().slice(start, end);
  });

  protected getParentCategory(parentId?: string): Category | undefined {
    if (!parentId) return undefined;
    return this.categoryStore.categories().find(c => c.categoryId === parentId);
  }

  handlePageChange(page: number) {
    this.currentPage.set(page);
  }

  handleItemsPerPageChange(size: number) {
    this.itemsPerPage.set(size);
    this.currentPage.set(1);
  }

  deleteCategory(category: Category) {
    this.delete.emit(category);
  }

}
