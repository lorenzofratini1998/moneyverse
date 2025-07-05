import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {
  CategoryDashboard,
} from '../../features/category/pages/category-dashboard/category-dashboard.model';
import {Observable} from 'rxjs';
import {DashboardFilterRequest} from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly httpClient = inject(HttpClient);

  private readonly baseUrl = environment.services.dashboardUrl;

  calculateCategoryDashboard(request: DashboardFilterRequest): Observable<CategoryDashboard> {
    return this.httpClient.post<CategoryDashboard>(`${this.baseUrl}/categories`, request);
  }
}
