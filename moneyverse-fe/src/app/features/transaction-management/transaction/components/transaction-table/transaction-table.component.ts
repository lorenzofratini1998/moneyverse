import {Component, input} from '@angular/core';
import {EnrichedTransaction} from '../../../transaction.model';
import {CurrencyPipe} from '@angular/common';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';

@Component({
  selector: 'app-transaction-table',
  imports: [
    CurrencyPipe,
    SvgComponent
  ],
  templateUrl: './transaction-table.component.html',
  styleUrl: './transaction-table.component.scss'
})
export class TransactionTableComponent {
  transactions = input.required<EnrichedTransaction[]>();
  protected readonly Icons = IconsEnum;
}
