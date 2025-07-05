import {Component, computed, input} from '@angular/core';
import {EnrichedCategoryStats} from '../../category-dashboard.model';
import {
  BarChartOptions,
  HorizontalBarChartComponent
} from '../../../../../../shared/components/horizontal-bar-chart/horizontal-bar-chart.component';

@Component({
  selector: 'app-category-horizontal-bar-chart',
  imports: [
    HorizontalBarChartComponent
  ],
  templateUrl: './category-horizontal-bar-chart.component.html',
  styleUrl: './category-horizontal-bar-chart.component.scss'
})
export class CategoryHorizontalBarChartComponent {
  readonly categoryStatistics = input.required<EnrichedCategoryStats[]>();

  barChartOptions = computed(() => {
    const labels = this.categoryStatistics().map(c => c.category.categoryName).reverse();
    const current = this.categoryStatistics().map(c => c.amount).reverse();

    return {
      labels,
      series: [
        {
          name: `Current`,
          data: current
        }
      ]
    } as BarChartOptions;
  })
}
