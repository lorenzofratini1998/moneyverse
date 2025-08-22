import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {CategoryStore} from '../../../../features/category/services/category.store';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';

@Component({
  selector: 'app-category-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    Select,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CategorySelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CategorySelectComponent),
      multi: true
    }
  ],
  templateUrl: './category-select.component.html',
  styleUrl: './category-select.component.scss'
})
export class CategorySelectComponent extends AbstractSelectComponent {
  override id = input<string>('category-select');
  override label = input<string>('Category');

  protected readonly categoryStore = inject(CategoryStore);

}
