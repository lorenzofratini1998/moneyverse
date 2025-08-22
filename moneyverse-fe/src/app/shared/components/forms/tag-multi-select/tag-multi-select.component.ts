import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {AbstractMultiSelectComponent} from '../abstract-multi-select.component';
import {TagStore} from '../../../../features/transaction/pages/tag-management/services/tag.store';
import {FloatLabel} from 'primeng/floatlabel';
import {MultiSelect} from 'primeng/multiselect';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-tag-multi-select',
  imports: [
    FloatLabel,
    MultiSelect,
    ReactiveFormsModule,
    LabelComponent,
    Message,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TagMultiSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => TagMultiSelectComponent),
      multi: true
    }
  ],
  templateUrl: './tag-multi-select.component.html'
})
export class TagMultiSelectComponent extends AbstractMultiSelectComponent {
  override id = input<string>('tag-multi-select');
  override label = input<string>('Tags');
  override display = input<string>('chip');
  override filter = input<boolean>(true);

  protected readonly tagStore = inject(TagStore);
}
