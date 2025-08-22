import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AbstractMultiSelectComponent} from '../abstract-multi-select.component';
import {CategoryStore} from '../../../../features/category/services/category.store';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {MultiSelect} from 'primeng/multiselect';

@Component({
  selector: 'app-category-multi-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    MultiSelect,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CategoryMultiSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CategoryMultiSelectComponent),
      multi: true
    }
  ],
  templateUrl: './category-multi-select.component.html'
})
export class CategoryMultiSelectComponent extends AbstractMultiSelectComponent {
  override id = input<string>('category-multi-select')
  override label = input<string>('Categories')

  protected readonly categoryStore = inject(CategoryStore);
}
