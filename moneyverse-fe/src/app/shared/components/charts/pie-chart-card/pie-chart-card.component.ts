import {Component, computed, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {AnalyticsService} from '../../../services/analytics.service';
import {SelectButton} from 'primeng/selectbutton';
import {FormsModule} from '@angular/forms';

import {ChartFilter, ChartFilterOption} from "../../../../features/analytics/analytics.models";
import {TranslationService} from '../../../services/translation.service';

@Component({
  selector: 'app-pie-chart-card',
  imports: [
    Card,
    SelectButton,
    FormsModule
  ],
  templateUrl: './pie-chart-card.component.html',
  styleUrl: './pie-chart-card.component.scss'
})
export class PieChartCardComponent {
  protected _pieChartFilter = signal<ChartFilter>('totalExpense')
  private readonly translateService = inject(TranslationService);

  chartFilterOptions = computed<ChartFilterOption[]>(() => {
    this.translateService.lang();
    return [
      {label: this.translateService.translate('app.income'), value: 'totalIncome'},
      {label: this.translateService.translate('app.expense'), value: 'totalExpense'}
    ]
  })

  pieChartFilter = this._pieChartFilter.asReadonly();

}
