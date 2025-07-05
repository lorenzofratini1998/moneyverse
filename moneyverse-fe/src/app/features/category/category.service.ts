import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {Category, CategoryRequest} from './category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = environment.services.budgetManagementUrl

  public getCategoriesByUser(userId: string): Observable<Category[]> {
    return this.httpClient.get<Category[]>(`${this.baseUrl}/categories/users/${userId}`);
  }

  public getDefaultCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(`${this.baseUrl}/categories?default=true`);
  }

  public createDefaultCategories(userId: string): Observable<Category[]> {
    return this.httpClient.post<Category[]>(`${this.baseUrl}/categories/users/${userId}/default`, {});
  }

  public createCategory(request: CategoryRequest): Observable<Category> {
    return this.httpClient.post<Category>(`${this.baseUrl}/categories`, request);
  }

  public updateCategory(categoryId: string, request: CategoryRequest): Observable<Category> {
    return this.httpClient.put<Category>(`${this.baseUrl}/categories/${categoryId}`, request);
  }

  public deleteCategory(categoryId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/categories/${categoryId}`);
  }
}
