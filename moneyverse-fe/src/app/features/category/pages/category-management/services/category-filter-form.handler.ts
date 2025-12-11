import {inject, Injectable} from '@angular/core';
import {CategoryCriteria} from '../../../category.model';
import {FormHandler} from '../../../../../shared/models/form.model';
import {FormBuilder, FormGroup} from "@angular/forms";
import {CategoryFilterStore} from './category-filter.store';
import {CategoryFilterFormData} from '../models/form.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryFilterFormHandler implements FormHandler<CategoryCriteria, CategoryFilterFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly categoryFilterStore = inject(CategoryFilterStore);

  create(): FormGroup {
    return this.fb.group({
      name: null,
      parentCategories: null,
    });
  }

  patch(form: FormGroup, criteria: CategoryCriteria): void {
    form.patchValue({
      name: criteria.name ?? null,
      parentCategories: criteria.parentCategories ?? null,
    }, {emitEvent: false});
  }

  reset(form: FormGroup): void {
    this.categoryFilterStore.resetFilters();
  }

  prepareData(form: FormGroup): CategoryFilterFormData {
    return form.value as CategoryFilterFormData;
  }

}
