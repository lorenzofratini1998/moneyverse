import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AccountStore} from '../../../../features/account/services/account.store';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-account-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    Select,
    FormsModule,
    TranslatePipe
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AccountSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AccountSelectComponent),
      multi: true
    }
  ],
  templateUrl: './account-select.component.html'
})
export class AccountSelectComponent extends AbstractSelectComponent {
  override id = input<string>('account-select');
  override label = input<string>('app.account');

  protected readonly accountStore = inject(AccountStore);
}
