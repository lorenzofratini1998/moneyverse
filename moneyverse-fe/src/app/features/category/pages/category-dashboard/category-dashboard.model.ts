import {Category} from "../../category.model";

export enum PeriodDashboardEnum {
  MONTH,
  YEAR,
  CUSTOM
}

export interface TrendDto {
  period: Date,
  amount: number,
  percentage: number,
}

export interface PeriodDashboard {
  period: PeriodDashboardEnum,
  month?: number,
  year?: number,
  startDate?: Date,
  endDate?: Date,
}

export interface CategoryKPI {
  period: PeriodDashboard,
  totalAmount: number,
  numberOfActiveCategories: number,
  averageAmount: number,
  previous?: CategoryKPI
}

export interface CategoryStatistics {
  categoryId: string,
  period: PeriodDashboard,
  amount: number,
  percentage: number,
  data: TrendDto[],
  previous?: CategoryStatistics
}

export interface CategoryDashboard {
  period: PeriodDashboard,
  comparePeriod?: PeriodDashboard,
  kpi?: CategoryKPI,
  topCategory?: CategoryStatistics,
  categories?: CategoryStatistics[]
}

export type EnrichedCategoryStats = CategoryStatistics & { category: Category };

export type EnrichedCategoryDashboard =
  Omit<CategoryDashboard, 'categories' | 'topCategory'> & {
  categories: EnrichedCategoryStats[];
  topCategory?: EnrichedCategoryStats;
};
