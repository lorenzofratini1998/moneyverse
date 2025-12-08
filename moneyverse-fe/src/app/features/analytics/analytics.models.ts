export type PeriodFormat = 'none' | 'month' | 'year' | 'custom';

export interface PeriodFormatOption {
  label: string,
  value: PeriodFormat
}

export type ChartFilter = 'totalAmount' | 'totalExpense' | 'totalIncome';

export interface ChartFilterOption {
  label: string,
  value: ChartFilter
}

export interface Period {
  startDate: Date,
  endDate: Date
}

export interface Amount {
  period?: Period,
  amount: number,
  variation?: number
}

export interface Count {
  count: number,
  variation?: number
}

export interface ExpenseIncome {
  period?: Period,
  expense: Amount,
  income: Amount,
  total: Amount
}

export interface DashboardFilter {
  userId: string
  periodFormat: PeriodFormat,
  period: Period,
  comparePeriodFormat: PeriodFormat,
  comparePeriod?: Period,
  accounts?: string[],
  categories?: string[],
  currency?: string,
  tags?: string[],
}

export interface AnalyticsPeriodSelector {
  format: PeriodFormat,
  value?: Period
}

export interface AnalyticsFilterFormData {
  period: AnalyticsPeriodSelector,
  comparePeriod?: AnalyticsPeriodSelector,
  accounts?: string[],
  categories?: string[],
  currency?: string,
  tags?: string[],
}
