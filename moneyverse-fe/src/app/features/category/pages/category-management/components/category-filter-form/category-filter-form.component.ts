import {Component, effect, inject} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {CategoryCriteria} from '../../../../category.model';
import {CategoryFilterFormData} from '../../models/form.model';
import {CategoryFilterFormHandler} from '../../services/category-filter-form.handler';
import {CategoryFilterStore} from '../../services/category-filter.store';
import {
  CategoryMultiSelectComponent
} from '../../../../../../shared/components/forms/category-multi-select/category-multi-select.component';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {ReactiveFormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-category-filter-form',
  imports: [
    CategoryMultiSelectComponent,
    InputTextComponent,
    ReactiveFormsModule,
    TranslatePipe
  ],
  templateUrl: './category-filter-form.component.html'
})
export class CategoryFilterFormComponent extends AbstractFormComponent<CategoryCriteria, CategoryFilterFormData> {
  protected override readonly formHandler = inject(CategoryFilterFormHandler);
  protected readonly categoryFilterStore = inject(CategoryFilterStore);

  constructor() {
    super();
    effect(() => {
      const criteria = this.categoryFilterStore.criteria();
      this.patch(criteria);
    });
  }

  override submit(): void {
    const formData = this.prepareData();
    this.categoryFilterStore.updateFilters({
      name: formData.name,
      parentCategories: formData.parentCategories
    });
  }
}
