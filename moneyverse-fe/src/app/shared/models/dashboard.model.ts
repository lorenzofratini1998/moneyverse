import {PeriodDashboard} from '../../features/category/pages/category-dashboard/category-dashboard.model';
import {BoundCriteria} from './criteria.model';

export interface DashboardFilter {
  accounts?: string[],
  categories?: string[],
  period?: PeriodDashboard,
  comparePeriod?: PeriodDashboard,
  amount?: BoundCriteria
}

export interface DashboardFilterRequest {
  userId: string,
  accounts?: string[],
  categories?: string[],
  period: PeriodDashboard,
  comparePeriod?: PeriodDashboard,
}
