import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {AccountStore} from '../../../../features/account/services/account.store';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';
import {LabelComponent} from '../label/label.component';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-account-category-select',
  imports: [
    FloatLabel,
    Message,
    ReactiveFormsModule,
    Select,
    FormsModule,
    LabelComponent,
    TranslatePipe
  ],
  templateUrl: './account-category-select.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AccountCategorySelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AccountCategorySelectComponent),
      multi: true
    }
  ]
})
export class AccountCategorySelectComponent extends AbstractSelectComponent {

  override id = input<string>('account-category-select');
  override label = input<string>('app.form.accountCategory');

  protected readonly accountStore = inject(AccountStore);

}
