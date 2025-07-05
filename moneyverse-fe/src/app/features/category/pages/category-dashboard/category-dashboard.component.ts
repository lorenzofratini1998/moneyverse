import {Component, computed, effect, inject, signal} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {DashboardService} from '../../../../shared/services/dashboard.service';
import {
  CategoryDashboard,
  EnrichedCategoryDashboard,
  EnrichedCategoryStats,
  PeriodDashboardEnum
} from './category-dashboard.model';
import {CategoryStore} from '../../category.store';
import {CategoryKpiComponent} from './components/category-kpi/category-kpi.component';
import {
  CategoryHorizontalBarChartComponent
} from './components/category-horizontal-bar-chart/category-horizontal-bar-chart.component';
import {CategoryLineChartComponent} from './components/category-line-chart/category-line-chart.component';
import {DashboardStore} from '../../../../shared/stores/dashboard.store';

@Component({
  selector: 'app-category-dashboard',
  imports: [
    CategoryKpiComponent,
    CategoryHorizontalBarChartComponent,
    CategoryLineChartComponent
  ],
  templateUrl: './category-dashboard.component.html',
  styleUrl: './category-dashboard.component.scss'
})
export class CategoryDashboardComponent {

  private readonly dashboardService = inject(DashboardService);
  private readonly authService = inject(AuthService);
  private readonly categoryStore = inject(CategoryStore);
  private readonly dashboardStore = inject(DashboardStore);

  readonly categoryDashboard = signal<CategoryDashboard>({} as CategoryDashboard);

  constructor() {
    effect(() => {
      this.dashboardService
        .calculateCategoryDashboard({
          userId: this.authService.getAuthenticatedUser().userId,
          accounts: this.dashboardStore.selectedAccounts()?.map(acc => acc.accountId),
          categories: this.dashboardStore.selectedCategories()?.map(cat => cat.categoryId),
          period: {
            period: PeriodDashboardEnum.CUSTOM,
            startDate: new Date('1970-01-01'),
            endDate: new Date('2025-12-31'),
          },
        })
        .subscribe(response => this.categoryDashboard.set(response));
    })
  }

  readonly categoryDashboardEnriched = computed<EnrichedCategoryDashboard>(() => {
    const base = this.categoryDashboard();
    const allCats = this.categoryStore.categories();

    if (!base.categories?.length) {
      return {
        ...base,
        categories: [],
        topCategory: undefined
      };
    }

    const categories = base.categories.map(stat => ({
      ...stat,
      category: allCats.find(c => c.categoryId === stat.categoryId)!
    })) as EnrichedCategoryStats[];

    const topCategory = base.topCategory
      ? ({
        ...base.topCategory,
        category: allCats.find(c => c.categoryId === base.topCategory!.categoryId)!
      } as EnrichedCategoryStats)
      : undefined;

    return {
      ...base,
      categories,
      topCategory
    };
  });
}
