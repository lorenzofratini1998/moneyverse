import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AbstractMultiSelectComponent} from '../abstract-multi-select.component';
import {AccountStore} from '../../../../features/account/services/account.store';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {MultiSelect} from 'primeng/multiselect';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-account-multi-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    MultiSelect,
    FormsModule,
    TranslatePipe
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AccountMultiSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AccountMultiSelectComponent),
      multi: true
    }
  ],
  templateUrl: './account-multi-select.component.html'
})
export class AccountMultiSelectComponent extends AbstractMultiSelectComponent {
  override id = input<string>('account-multi-select')
  override label = input<string>('app.accounts')

  protected readonly accountStore = inject(AccountStore);
}
