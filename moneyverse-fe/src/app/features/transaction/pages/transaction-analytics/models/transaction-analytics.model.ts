import {Amount, Count, ExpenseIncome, Period} from '../../../../analytics/analytics.models';
import {BoundCriteria} from '../../../../../shared/models/criteria.model';

export interface TransactionKpi {
  period: Period,
  numberOfTransactions: Count,
  totalIncome: Amount,
  totalExpense: Amount,
  averageAmount: Amount,
  quantile90: Amount
  compare: TransactionKpi
}

export interface DistributionRange {
  range: BoundCriteria,
  count: Count
}

export interface TransactionDistribution {
  period: Period,
  data: DistributionRange[],
  compare: TransactionDistribution
}

export interface TransactionTrend {
  period: Period,
  data: ExpenseIncome[],
  compare: TransactionTrend
}
