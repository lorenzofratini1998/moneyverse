import {Component, computed, inject, input, output, signal, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Category} from '../../../../category.model';
import {TableModule} from 'primeng/table';
import {FormsModule} from '@angular/forms';
import {CategoryTreeDialogComponent} from '../category-tree-dialog/category-tree-dialog.component';
import {ChipComponent} from '../../../../../../shared/components/chip/chip.component';
import {TableAction, TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {TableActionsComponent} from '../../../../../../shared/components/table-actions/table-actions.component';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';

@Component({
  selector: 'app-category-table',
  imports: [
    TableModule,
    FormsModule,
    CategoryTreeDialogComponent,
    ChipComponent,
    TableComponent,
    CellTemplateDirective,
    TableActionsComponent
  ],
  templateUrl: './category-table.component.html'
})
export class CategoryTableComponent {
  categories = input.required<Category[]>();

  onDelete = output<Category>();
  onEdit = output<Category>();

  protected readonly Icons = IconsEnum;
  private readonly confirmationService = inject(AppConfirmationService);

  categoryTreeDialog = viewChild.required<CategoryTreeDialogComponent>(CategoryTreeDialogComponent);

  parentCategories = computed<Category[]>(() => {
    return this.categories().filter(c => !c.parentCategory);
  });

  config = computed<TableConfig<Category>>(() => ({
    currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
    dataKey: 'categoryId',
    paginator: true,
    rows: 10,
    rowsPerPageOptions: [5, 10, 25, 50],
    showCurrentPageReport: true,
    stripedRows: true,
    styleClass: 'mt-4'
  }));

  columns = signal<TableColumn<Category>[]>([
    {field: 'categoryName', header: 'Name', sortable: true},
    {field: 'description', header: 'Description'},
    {field: 'parentCategory', header: 'Parent Category', sortable: true},
  ])

  protected getParentCategory(parentId?: string): Category | undefined {
    if (!parentId) return undefined;
    return this.categories().find(c => c.categoryId === parentId);
  }

  actions = computed<TableAction<Category>[]>(() => [
    {
      icon: IconsEnum.NETWORK,
      severity: 'success',
      visible: (cat) => !cat.parentCategory && (cat.children ?? []).length > 0,
      click: (cat) => this.categoryTreeDialog().open(cat),
    },
    {
      icon: IconsEnum.PENCIL,
      severity: 'secondary',
      click: (cat) => this.onEdit.emit(cat),
    },
    {
      icon: IconsEnum.TRASH,
      severity: 'danger',
      click: (cat) => this.confirmDelete(cat)
    }
  ])

  private confirmDelete(category: Category) {
    this.confirmationService.confirmDelete({
      header: 'Delete Category',
      message: `Are you sure you want to delete the category "${category.categoryName}?" All associated transactions will be deleted.`,
      accept: () => this.onDelete.emit(category)
    })
  }

}
