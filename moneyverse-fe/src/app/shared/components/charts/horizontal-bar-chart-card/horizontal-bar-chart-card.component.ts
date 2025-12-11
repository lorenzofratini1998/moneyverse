import {Component, inject, signal} from '@angular/core';
import {AnalyticsService} from '../../../services/analytics.service';
import {Card} from 'primeng/card';
import {SelectButton} from 'primeng/selectbutton';
import {FormsModule} from '@angular/forms';
import {ChartFilter} from "../../../../features/analytics/analytics.models";

@Component({
  selector: 'app-horizontal-bar-chart-card',
  imports: [
    Card,
    SelectButton,
    FormsModule
  ],
  templateUrl: './horizontal-bar-chart-card.component.html',
  styleUrl: './horizontal-bar-chart-card.component.scss'
})
export class HorizontalBarChartCardComponent {
  protected readonly analyticsService = inject(AnalyticsService);
  protected _pieChartFilter = signal<ChartFilter>('totalAmount')

  pieChartFilter = this._pieChartFilter.asReadonly();
}
