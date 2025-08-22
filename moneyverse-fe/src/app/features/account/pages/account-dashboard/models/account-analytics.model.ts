import {Amount, Count, ExpenseIncome, Period} from "../../../../analytics/analytics.models";

export interface AccountAnalyticsKpi {
  period: Period,
  totalAmount: Amount,
  numberOfActiveAccounts: Count,
  mostUsedAccount: string,
  leastUsedAccount: string
  compare: AccountAnalyticsKpi
}

export interface AccountAnalyticsDistribution {
  period: Period,
  accountId: string,
  totalIncome: Amount,
  totalExpense: Amount,
  totalAmount: Amount,
  compare: AccountAnalyticsDistribution
}

export interface AccountAnalyticsTrend {
  period: Period,
  accountId: string,
  data: ExpenseIncome[],
  compare: AccountAnalyticsTrend
}
