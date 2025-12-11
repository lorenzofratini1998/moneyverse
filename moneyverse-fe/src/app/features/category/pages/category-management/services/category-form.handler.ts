import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {Category} from '../../../category.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {IconsEnum} from '../../../../../shared/models/icons.model';
import {CategoryFormData} from '../models/form.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryFormHandler implements FormHandler<Category, CategoryFormData> {
  private readonly fb = inject(FormBuilder);

  create(): FormGroup {
    return this.fb.group({
      categoryId: [null],
      categoryName: [null, Validators.required],
      parentCategory: [null],
      description: [null],
      color: ['red'],
      icon: [IconsEnum.CIRCLE_DOLLAR_SIGN]
    });
  }

  patch(form: FormGroup, category: Category): void {
    form.patchValue({
      categoryId: category.categoryId,
      categoryName: category.categoryName,
      parentCategory: category.parentCategory ?? null,
      description: category.description,
      color: category.style.color,
      icon: category.style.icon
    })
  }

  reset(form: FormGroup): void {
    form.reset({
      categoryId: null,
      categoryName: '',
      parentCategory: null,
      description: '',
      color: 'red',
      icon: IconsEnum.CIRCLE_DOLLAR_SIGN,
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): CategoryFormData {
    const value = form.value;
    return {
      categoryId: value.categoryId,
      categoryName: value.categoryName,
      parentId: value.parentCategory,
      description: value.description,
      style: {
        color: value.color,
        icon: value.icon
      }
    };
  }

}
