import {Component, computed, inject, viewChild} from '@angular/core';
import {CategoryFormDialogComponent} from "./components/category-form-dialog/category-form-dialog.component";
import {CategoryDefaultDialogComponent} from "./components/category-default-dialog/category-default-dialog.component";
import {CategoryStore} from '../../services/category.store';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {CategoryTableComponent} from './components/category-table/category-table.component';
import {AuthService} from '../../../../core/auth/auth.service';
import {Category} from '../../category.model';
import {DialogService} from '../../../../shared/services/dialog.service';
import {CategoryFilterStore} from './services/category-filter.store';
import {CategoryFormData} from './models/form.model';
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';
import {CategoryFilterPanelComponent} from './components/category-filter-panel/category-filter-panel.component';

@Component({
  selector: 'app-category-management',
  imports: [
    CategoryFormDialogComponent,
    CategoryDefaultDialogComponent,
    CategoryTableComponent,
    ManagementComponent,
    CategoryFilterPanelComponent
  ],
  providers: [DialogService],
  templateUrl: './category-management.component.html'
})
export class CategoryManagementComponent {

  protected readonly categoryStore = inject(CategoryStore);
  protected readonly categoryFilterStore = inject(CategoryFilterStore);
  protected readonly authService = inject(AuthService);

  defaultCategoryDialog = viewChild.required<CategoryDefaultDialogComponent>(CategoryDefaultDialogComponent);
  categoryFormDialog = viewChild.required<CategoryFormDialogComponent>(CategoryFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => ({
    title: 'Category Management',
    actions: [
      {
        icon: IconsEnum.BOX,
        severity: 'secondary',
        label: 'Add Default Categories',
        condition: () => this.categoryStore.defaultCategories().length > 0,
        action: () => this.defaultCategoryDialog().open()
      },
      {
        icon: IconsEnum.REFRESH,
        variant: 'text',
        severity: 'secondary',
        action: () => this.categoryStore.loadCategories(true)
      },
      {
        icon: IconsEnum.PLUS,
        label: 'New Category',
        action: () => this.categoryFormDialog().open()
      }
    ]
  }))

  submit(formData: CategoryFormData) {
    const categoryId = formData.categoryId;
    if (categoryId) {
      this.categoryStore.updateCategory({
        categoryId,
        request: {...formData}
      })
    } else {
      this.categoryStore.createCategory({
          ...formData,
          userId: this.authService.authenticatedUser.userId,
        }
      )
    }
  }

  createDefaultCategories(): void {
    this.categoryStore.createDefaultCategories(this.authService.authenticatedUser.userId);
  }

  deleteCategory(category: Category) {
    this.categoryStore.deleteCategory(category.categoryId);
  }
}
