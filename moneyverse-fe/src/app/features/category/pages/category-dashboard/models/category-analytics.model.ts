import {Amount, Count, ExpenseIncome, Period} from "../../../../analytics/analytics.models";

export interface CategoryAnalyticsKpi {
  period: Period,
  topCategory: string,
  activeCategories: Count,
  mostUsedCategory: string,
  uncategorizedAmount: Amount
}

export interface CategoryAnalyticsDistribution {
  period: Period,
  categoryId: string,
  totalIncome: Amount,
  totalExpense: Amount,
  totalAmount: Amount,
  compare: CategoryAnalyticsDistribution
}

export interface CategoryAnalyticsTrend {
  period: Period,
  categoryId: string,
  data: ExpenseIncome[],
  compare: CategoryAnalyticsTrend
}
