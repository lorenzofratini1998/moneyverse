import {Component, computed, effect, inject, signal} from '@angular/core';
import {LineChartComponent} from '../../../../../../shared/components/charts/line-chart/line-chart.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CategoryStore} from '../../../../services/category.store';
import {AnalyticsService} from '../../../../../../shared/services/analytics.service';
import {Category} from '../../../../category.model';
import {Card} from 'primeng/card';
import {Select} from 'primeng/select';
import {SelectButton} from 'primeng/selectbutton';
import {
  AbstractLineChartComponent
} from '../../../../../../shared/components/charts/line-chart/abstract-line-chart.component';
import {CategoryTrendChartService} from '../../services/category-trend-chart.service';
import {ChartFilter} from '../../../../../analytics/analytics.models';
import {LineChartOptions} from "../../../../../../shared/models/chart.model";

@Component({
  selector: 'app-category-trend-chart',
  imports: [
    LineChartComponent,
    ReactiveFormsModule,
    FormsModule,
    Card,
    Select,
    SelectButton
  ],
  templateUrl: './category-trend-chart.component.html'
})
export class CategoryTrendChartComponent extends AbstractLineChartComponent<Category> {
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly analyticsService = inject(AnalyticsService);
  private readonly chartService = inject(CategoryTrendChartService);

  protected chartFilter = signal<ChartFilter>('totalAmount');
  protected selectedCategory = signal<Category | null>(null);

  override options = computed(() => {
    const trend = this.chartService.data();
    if (!trend || trend.length === 0 || this.selectedCategory() === null) {
      return {
        series: [{
          name: 'No data',
          data: []
        }]
      } as LineChartOptions;
    }
    return {
      labels: this.chartService.getLabels(trend, this.selectedCategory()!),
      series: this.chartService.getSeries(trend, this.selectedCategory()!, this.chartFilter()),
    } as LineChartOptions
  })

  constructor() {
    super()
    effect(() => {
      this.selectedCategory.set(this.categoryStore.categories()[0])
    });
  }

  override clickChart(event: any) {
    if (this.selectedCategory() && this.selectedCategory()?.categoryName == event.seriesName) {
      this.onChartClick.emit([this.selectedCategory()!]);
    }
  }
}
