import {Component, inject, ViewChild} from '@angular/core';
import {CategoryFormDialogComponent} from "./components/category-form-dialog/category-form-dialog.component";
import {CategoryDefaultDialogComponent} from "./components/category-default-dialog/category-default-dialog.component";
import {SvgComponent} from "../../../../shared/components/svg/svg.component";
import {ToastEnum} from "../../../../shared/components/toast/toast.component";
import {CategoryStore} from '../../category.store';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {CategoryTableComponent} from './components/category-table/category-table.component';
import {AuthService} from '../../../../core/auth/auth.service';
import {switchMap, take} from 'rxjs';
import {Category, CategoryForm, CategoryFormData, CategoryRequest} from '../../category.model';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {Toast} from 'primeng/toast';
import {ConfirmationService, MessageService} from 'primeng/api';
import {CategoryService} from '../../category.service';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-category-management',
  imports: [
    CategoryFormDialogComponent,
    CategoryDefaultDialogComponent,
    SvgComponent,
    CategoryTableComponent,
    Toast,
    Button
  ],
  templateUrl: './category-management.component.html',
  styleUrl: './category-management.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class CategoryManagementComponent {

  protected readonly IconsEnum = IconsEnum;
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly authService = inject(AuthService);
  protected readonly categoryService = inject(CategoryService);
  private readonly messageService = inject(MessageService);

  @ViewChild(CategoryDefaultDialogComponent) defaultCategoryModal!: CategoryDefaultDialogComponent;
  @ViewChild(CategoryFormDialogComponent) categoryForm!: CategoryFormDialogComponent;
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;

  createDefaultCategories(): void {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.categoryService.createDefaultCategories(userId))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Default categories created successfully.'
        })
        this.categoryStore.refreshCategories()
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error creating default categories.'
        })
      },

    })
  }

  createCategory(formData: CategoryFormData) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.categoryService.createCategory(this.createCategoryRequest(userId, formData)))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Category created successfully.'
        });
        this.categoryStore.refreshCategories();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the category creation.'
        });
      }
    })
  }

  private createCategoryRequest(userId: string, formData: CategoryFormData): CategoryRequest {
    return {
      userId: userId,
      categoryName: formData.categoryName,
      parentId: formData.parentCategory,
      description: formData.description,
      style: {
        color: formData.style.color,
        icon: formData.style.icon
      }
    };
  }

  editCategory(categoryForm: CategoryForm) {
    this.categoryService.updateCategory(categoryForm.categoryId!, this.createCategoryUpdateRequest(categoryForm.formData)).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Category updated successfully.'
        });
        this.categoryStore.refreshCategories();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the category update.'
        });
      }
    })
  }

  private createCategoryUpdateRequest(formData: CategoryFormData) {
    const request: Partial<CategoryRequest> = {};
    request.categoryName = formData.categoryName;
    request.parentId = formData.parentCategory;
    request.description = formData.description;
    request.style = {
      color: formData.style.color,
      icon: formData.style.icon
    };
    return request;
  }

  deleteCategory(category: Category) {
    this.categoryService.deleteCategory(category.categoryId).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Category deleted successfully.'
        });
        this.categoryStore.refreshCategories();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the category deletion.'
        });
      }
    })
  }
}
