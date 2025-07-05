import {Component, inject, signal, ViewChild} from '@angular/core';
import {CategoryFormComponent} from "./components/category-form/category-form.component";
import {
  DefaultCategoryModalComponent
} from "./components/default-category-modal/default-category-modal.component";
import {DialogComponent} from "../../../../shared/components/dialog/dialog.component";
import {SvgComponent} from "../../../../shared/components/svg/svg.component";
import {ToastComponent, ToastEnum} from "../../../../shared/components/toast/toast.component";
import {CategoryStore} from '../../category.store';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {CategoryTableComponent} from './components/category-table/category-table.component';
import {AuthService} from '../../../../core/auth/auth.service';
import {CategoryService} from '../../category.service';
import {MessageService} from '../../../../shared/services/message.service';
import {finalize} from 'rxjs';
import {Category} from '../../category.model';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-category-management',
  imports: [
    CategoryFormComponent,
    DefaultCategoryModalComponent,
    DialogComponent,
    SvgComponent,
    ToastComponent,
    CategoryTableComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './category-management.component.html',
  styleUrl: './category-management.component.scss'
})
export class CategoryManagementComponent {

  protected readonly IconsEnum = IconsEnum;
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly authService = inject(AuthService);
  protected readonly categoryService = inject(CategoryService);
  private readonly messageService = inject(MessageService);

  protected showAddDefaultCategoryModal = signal<boolean>(false);

  @ViewChild(CategoryFormComponent) categoryForm!: CategoryFormComponent;
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;

  createDefaultCategories(): void {
    this.categoryService.createDefaultCategories(this.authService.getAuthenticatedUser().userId)
      .pipe(
        finalize(() => {
          this.showAddDefaultCategoryModal.set(false);
        })
      )
      .subscribe({
        next: () => {
          this.messageService.showMessage({
            type: ToastEnum.SUCCESS,
            message: 'Default categories created successfully.'
          })
          this.categoryStore.refreshCategories()
        },
        error: () => {
          this.messageService.showMessage({
            type: ToastEnum.ERROR,
            message: 'Error creating default categories.'
          })
        },

      })
  }

  saveCategory(formData: any) {
    const userId = this.authService.getAuthenticatedUser().userId;

    const saveOperation = this.categoryStore.selectedCategory() !== null
      ? this.categoryService.updateCategory(this.categoryStore.selectedCategory()!.categoryId, this.createCategoryUpdateRequest(formData))
      : this.categoryService.createCategory(this.createCategoryRequest(userId, formData));

    saveOperation.subscribe({
      next: () => {
        this.categoryStore.closeForm();
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: 'Category created successfully.'
        });
        this.categoryForm.reset();
        this.categoryStore.refreshCategories();
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: 'Error during the category creation.'
        });
      }
    })
  }

  private createCategoryRequest(userId: string, formData: any) {
    return {
      userId: userId,
      categoryName: formData.categoryName,
      parentId: formData.parentCategory,
      description: formData.description,
      style: {
        backgroundColor: formData.backgroundColor,
        textColor: formData.textColor,
        icon: formData.icon
      }
    };
  }

  private createCategoryUpdateRequest(formData: any) {
    return {
      categoryName: formData.categoryName,
      parentId: formData.parentCategory,
      description: formData.description,
      style: {
        backgroundColor: formData.backgroundColor,
        textColor: formData.textColor,
        icon: formData.icon
      }
    };
  }

  onDeleteCategory(category: Category) {
    this.confirmDialog.show();
    this.confirmDialog.confirm.subscribe(result => {
      if (result) {
        this.deleteCategory(category);
      }
    });
  }

  deleteCategory(category: Category) {
    this.categoryService.deleteCategory(category.categoryId)
      .subscribe({
        next: () => {
          this.categoryStore.refreshCategories()
          this.messageService.showMessage({
            type: ToastEnum.SUCCESS,
            message: 'Category deleted successfully.'
          });
        },
        error: () => {
          this.messageService.showMessage({
            type: ToastEnum.ERROR,
            message: 'Error during the category deletion.'
          });
        }
      })
  }
}
