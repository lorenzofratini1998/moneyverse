import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {Budget, BudgetRequest, Category, CategoryRequest} from '../category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly httpClient = inject(HttpClient);

  public getCategoriesByUser(userId: string): Observable<Category[]> {
    return this.httpClient.get<Category[]>(`/categories/users/${userId}`);
  }

  public getDefaultCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(`/categories?default=true`);
  }

  public createDefaultCategories(userId: string): Observable<Category[]> {
    return this.httpClient.post<Category[]>(`/categories/users/${userId}/default`, {});
  }

  public createCategory(request: CategoryRequest): Observable<Category> {
    return this.httpClient.post<Category>(`/categories`, request);
  }

  public updateCategory(categoryId: string, request: CategoryRequest): Observable<Category> {
    return this.httpClient.put<Category>(`/categories/${categoryId}`, request);
  }

  public deleteCategory(categoryId: string): Observable<void> {
    return this.httpClient.delete<void>(`/categories/${categoryId}`);
  }

  public createBudget(request: BudgetRequest): Observable<Budget> {
    return this.httpClient.post<Budget>(`/budgets`, request);
  }

  public getBudgetsByUser(userId: string): Observable<Budget[]> {
    return this.httpClient.get<Budget[]>(`/budgets/users/${userId}`);
  }

  public getBudget(budgetId: string): Observable<Budget> {
    return this.httpClient.get<Budget>(`/budgets/${budgetId}`);
  }

  public updateBudget(budgetId: string, request: BudgetRequest): Observable<Budget> {
    return this.httpClient.put<Budget>(`/budgets/${budgetId}`, request);
  }

  public deleteBudget(budgetId: string): Observable<void> {
    return this.httpClient.delete<void>(`/budgets/${budgetId}`);
  }

}
