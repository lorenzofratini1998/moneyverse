import {Component, computed, effect, input, signal} from '@angular/core';
import {EnrichedCategoryStats} from '../../category-dashboard.model';
import {
  LineChartComponent,
  LineChartOptions
} from '../../../../../../shared/components/line-chart/line-chart.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-category-line-chart',
  imports: [
    LineChartComponent,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './category-line-chart.component.html',
  styleUrl: './category-line-chart.component.scss'
})
export class CategoryLineChartComponent {
  readonly categoryStatistics = input.required<EnrichedCategoryStats[]>();
  readonly activeCategories = computed(() => this.categoryStatistics().map(c => c.category));

  selectedCategoryName = signal<string | null>(null);

  constructor() {
    effect(() => {
      const categories = this.activeCategories();
      if (categories.length > 0 && !this.selectedCategoryName()) {
        this.selectedCategoryName.set(categories[0].categoryName);
      }
    });
  }

  readonly categorySelected = computed(() => {
    const stats = this.categoryStatistics();
    const selectedName = this.selectedCategoryName();
    return stats.find(c => c.category.categoryName === selectedName) ?? stats[0];
  });

  readonly lineChartOptions = computed(() => {
    const selected = this.categorySelected();
    if (!selected) {
      return {
        labels: [],
        series: [{
          name: 'No data',
          data: []
        }]
      } as LineChartOptions;
    }

    const labels = this.categorySelected().data.map(t => t.period.toString());
    const currentData = this.categorySelected().data.map(t => t.amount);

    return {
      labels,
      series: [
        {
          name: `${this.categorySelected().category.categoryName} - Current`,
          data: currentData
        }
      ]
    } as LineChartOptions
  })
}
