import {Component, inject, signal, ViewChild} from '@angular/core';
import {CategoryService} from '../category.service';
import {AuthService} from '../../../core/auth/auth.service';
import {combineLatest, finalize, shareReplay, startWith, Subject, switchMap} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {Category} from '../budget.model';
import {LucideAngularModule} from 'lucide-angular';
import {DialogComponent} from '../../../shared/components/dialog/dialog.component';
import {DefaultCategoryModalComponent} from './components/default-category-modal/default-category-modal.component';
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {MessageService} from '../../../shared/services/message.service';
import {ToastComponent, ToastEnum} from '../../../shared/components/toast/toast.component';
import {CategoryFormComponent} from './components/category-form/category-form.component';
import {IconsEnum} from "../../../shared/models/icons.model";
import {CategoryTreeComponent} from './components/category-tree/category-tree.component';
import {CategoryStore} from '../category.store';

@Component({
  selector: 'app-category',
  imports: [
    LucideAngularModule,
    DialogComponent,
    DefaultCategoryModalComponent,
    SvgComponent,
    ToastComponent,
    CategoryFormComponent,
    CategoryTreeComponent
  ],
  templateUrl: './category.component.html'
})
export class CategoryComponent {
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly Icons = IconsEnum;
  private readonly categoryService = inject(CategoryService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);

  private readonly refreshCategories$ = new Subject<void>();
  private readonly userId$ = this.authService.getUserId().pipe(shareReplay(1));

  @ViewChild(CategoryFormComponent) categoryForm!: CategoryFormComponent;

  showAddDefaultCategoryModal = signal<boolean>(false);
  categories$ = toSignal(
    combineLatest([
      this.userId$,
      this.refreshCategories$.pipe(startWith(null))
    ]).pipe(
      switchMap(([userId]) => this.categoryService.getCategoriesByUser(userId))
    ),
    {initialValue: [] as Category[]}
  )

  createDefaultCategories(): void {
    this.userId$.pipe(
      switchMap(userId => this.categoryService.createDefaultCategories(userId)),
      finalize(() => {
        this.showAddDefaultCategoryModal.set(false);
      })
    ).subscribe({
      next: () => {
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: 'Default categories created successfully.'
        })
        this.refreshCategories$.next();
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
    this.userId$.pipe(
      switchMap(userId => {
        if (this.categoryStore.selectedCategory() !== null) {
          return this.categoryService.updateCategory(this.categoryStore.selectedCategory()!.categoryId, this.createCategoryUpdateRequest(formData));
        } else {
          return this.categoryService.createCategory(this.createCategoryRequest(userId, formData))
        }
      })
    ).subscribe({
      next: () => {
        this.categoryStore.closeForm();
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: 'Category created successfully.'
        });
        this.categoryForm.reset();
        this.refreshCategories$.next();
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
    this.categoryService
      .deleteCategory(category.categoryId)
      .pipe(finalize(() => this.refreshCategories$.next()))
      .subscribe({
        next: () => {
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
