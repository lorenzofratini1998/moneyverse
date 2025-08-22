import {Component, computed, inject, viewChild} from '@angular/core';
import {BarChartComponent} from '../../../../../../shared/components/charts/bar-chart/bar-chart.component';
import {CategoryStore} from '../../../../services/category.store';
import {CategoryAnalyticsDistribution} from '../../models/category-analytics.model';
import {
  HorizontalBarChartCardComponent
} from '../../../../../../shared/components/charts/horizontal-bar-chart-card/horizontal-bar-chart-card.component';
import {CategoryDistributionChartService} from '../../services/category-distribution-chart.service';
import {
  AbstractBarChartComponent
} from '../../../../../../shared/components/charts/bar-chart/abstract-bar-chart-component.directive';
import {Category} from '../../../../category.model';
import {BarChartOptions} from '../../../../../../shared/models/chart.model';

@Component({
  selector: 'app-category-distribution-chart',
  imports: [
    BarChartComponent,
    HorizontalBarChartCardComponent
  ],
  template: `
    <app-horizontal-bar-chart-card>
      <div chart-content>
        <app-bar-chart
          orientation="horizontal"
          [options]="options()"
          (onChartClick)="clickChart($event)"
        />
      </div>
    </app-horizontal-bar-chart-card>
  `
})
export class CategoryDistributionChartComponent extends AbstractBarChartComponent<Category> {
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly chartService = inject(CategoryDistributionChartService);

  horizontalBarChartCard = viewChild.required(HorizontalBarChartCardComponent);

  override options = computed<BarChartOptions>(() => {
    const categoryDistribution: CategoryAnalyticsDistribution[] = this.chartService.data();
    if (categoryDistribution.length === 0 || this.categoryStore.categoriesMap().size === 0) return {
      labels: [],
      series: [{
        name: 'No data',
        data: []
      }]
    } as BarChartOptions;
    return this.chartService.getChartOptions(categoryDistribution, this.horizontalBarChartCard().pieChartFilter());
  });

  clickChart(event: any) {
    this.onChartClick.emit([this.categoryStore.categories().find(cat => cat.categoryName === (event.name)) as Category]);
  }

}
