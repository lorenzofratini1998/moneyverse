import {Component, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {AnalyticsService} from '../../../services/analytics.service';
import {SelectButton} from 'primeng/selectbutton';
import {FormsModule} from '@angular/forms';

import {ChartFilter} from "../../../../features/analytics/analytics.models";

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
  protected readonly analyticsService = inject(AnalyticsService);
  protected _pieChartFilter = signal<ChartFilter>('totalAmount')

  pieChartFilter = this._pieChartFilter.asReadonly();

}
