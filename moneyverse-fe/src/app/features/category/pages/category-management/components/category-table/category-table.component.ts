import {Component, computed, inject, input, output, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Category, CategoryForm, CategoryFormData} from '../../../../category.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {TableModule} from 'primeng/table';
import {MultiSelect} from 'primeng/multiselect';
import {FormsModule} from '@angular/forms';
import {ButtonDirective} from 'primeng/button';
import {Chip} from 'primeng/chip';
import {CategoryFormDialogComponent} from '../category-form-dialog/category-form-dialog.component';
import {ConfirmationService, MessageService} from 'primeng/api';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {CategoryTreeDialogComponent} from '../category-tree-dialog/category-tree-dialog.component';
import {ColorService} from '../../../../../../shared/services/color.service';

@Component({
  selector: 'app-category-table',
  imports: [
    SvgComponent,
    TableModule,
    MultiSelect,
    FormsModule,
    ButtonDirective,
    Chip,
    CategoryFormDialogComponent,
    ConfirmDialog,
    CategoryTreeDialogComponent
  ],
  templateUrl: './category-table.component.html',
  styleUrl: './category-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class CategoryTableComponent {
  protected readonly Icons = IconsEnum;
  protected readonly colorService = inject(ColorService);
  private readonly confirmationService = inject(ConfirmationService);
  categories = input.required<Category[]>();

  protected searchValue: string | undefined;

  deleted = output<Category>();
  edited = output<CategoryForm>();

  @ViewChild(CategoryFormDialogComponent) categoryForm!: CategoryFormDialogComponent;
  @ViewChild(CategoryTreeDialogComponent) categoryTree!: CategoryTreeDialogComponent;

  parentCategories = computed<Category[]>(() => {
    return this.categories().filter(c => !c.parentCategory);
  });

  protected getParentCategory(parentId?: string): Category | undefined {
    if (!parentId) return undefined;
    return this.categories().find(c => c.categoryId === parentId);
  }

  onDelete(event: Event, category: Category) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure you want to delete the category ${category.categoryName.toUpperCase()}? All associated transactions and subcategories will be deleted.`,
      header: 'Delete category',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
      },
      accept: () => {
        this.deleteCategory(category);
      },
    })
  }

  private deleteCategory(category: Category) {
    this.deleted.emit(category);
  }

  onEdit(formData: CategoryFormData) {
    this.edited.emit({
      categoryId: this.categoryForm.categoryToEdit()?.categoryId,
      formData: formData
    });
  }

}
