import {ExpenseIncome, Period} from '../../analytics/analytics.models';

export interface OverviewAnalytics {
  period: Period,
  total: ExpenseIncome,
  data: ExpenseIncome[]
}
