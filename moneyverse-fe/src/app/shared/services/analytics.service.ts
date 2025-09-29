import {computed, inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {
  AccountAnalyticsDistribution,
  AccountAnalyticsKpi,
  AccountAnalyticsTrend
} from '../../features/account/pages/account-dashboard/models/account-analytics.model';
import {
  CategoryAnalyticsDistribution,
  CategoryAnalyticsKpi,
  CategoryAnalyticsTrend
} from '../../features/category/pages/category-dashboard/models/category-analytics.model';
import {ChartFilterOption, DashboardFilter} from "../../features/analytics/analytics.models";
import {
  TransactionDistribution,
  TransactionKpi,
  TransactionTrend
} from '../../features/transaction/pages/transaction-analytics/models/transaction-analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly httpClient = inject(HttpClient);

  chartFilterOptions = computed<ChartFilterOption[]>(() => [
    {label: 'Total Amount', value: 'totalAmount'},
    {label: 'Total Income', value: 'totalIncome'},
    {label: 'Total Expense', value: 'totalExpense'}
  ])

  calculateAccountAnalyticsKpi(filter: DashboardFilter): Observable<AccountAnalyticsKpi> {
    return this.httpClient.post<AccountAnalyticsKpi>(`analytics/accounts/kpi`, filter);
  }

  calculateAccountAnalyticsDistribution(filter: DashboardFilter): Observable<AccountAnalyticsDistribution[]> {
    return this.httpClient.post<AccountAnalyticsDistribution[]>(`analytics/accounts/distribution`, filter);
  }

  calculateAccountAnalyticsTrend(filter: DashboardFilter): Observable<AccountAnalyticsTrend[]> {
    return this.httpClient.post<AccountAnalyticsTrend[]>(`analytics/accounts/trend`, filter);
  }

  calculateCategoryKpi(filter: DashboardFilter): Observable<CategoryAnalyticsKpi> {
    return this.httpClient.post<CategoryAnalyticsKpi>(`analytics/categories/kpi`, filter);
  }

  calculateCategoryDistribution(filter: DashboardFilter): Observable<CategoryAnalyticsDistribution[]> {
    return this.httpClient.post<CategoryAnalyticsDistribution[]>(`analytics/categories/distribution`, filter);
  }

  calculateCategoryTrend(filter: DashboardFilter): Observable<CategoryAnalyticsTrend[]> {
    return this.httpClient.post<CategoryAnalyticsTrend[]>(`analytics/categories/trend`, filter);
  }

  calculateTransactionKpi(filter: DashboardFilter): Observable<TransactionKpi> {
    return this.httpClient.post<TransactionKpi>(`analytics/transactions/kpi`, filter);
  }

  calculateTransactionDistribution(filter: DashboardFilter): Observable<TransactionDistribution> {
    return this.httpClient.post<TransactionDistribution>(`analytics/transactions/distribution`, filter);
  }

  calculateTransactionTrend(filter: DashboardFilter): Observable<TransactionTrend> {
    return this.httpClient.post<TransactionTrend>(`analytics/transactions/trend`, filter);
  }
}
