import {Component, inject} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {Category} from '../../../../category.model';
import {CategoryFormHandler} from '../../services/category-form.handler';
import {
  CategorySelectComponent
} from '../../../../../../shared/components/forms/category-select/category-select.component';
import {ColorPickerComponent} from '../../../../../../shared/components/forms/color-picker/color-picker.component';
import {IconPickerComponent} from '../../../../../../shared/components/forms/icon-picker/icon-picker.component';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {ReactiveFormsModule} from '@angular/forms';
import {TextAreaComponent} from '../../../../../../shared/components/forms/text-area/text-area.component';
import {CategoryFormData} from "../../models/form.model";
import {FormPreviewComponent} from '../../../../../../shared/components/forms/form-preview/form-preview.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-category-form',
  imports: [
    CategorySelectComponent,
    ColorPickerComponent,
    IconPickerComponent,
    InputTextComponent,
    ReactiveFormsModule,
    TextAreaComponent,
    FormPreviewComponent,
    TranslatePipe
  ],
  templateUrl: './category-form.component.html'
})
export class CategoryFormComponent extends AbstractFormComponent<Category, CategoryFormData> {
  protected override readonly formHandler = inject(CategoryFormHandler);
}
