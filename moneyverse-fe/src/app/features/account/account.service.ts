import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Account, AccountCategory, AccountCriteria, AccountRequest} from './account.model';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = environment.services.accountManagementUrl

  public getAccountCategories(): Observable<AccountCategory[]> {
    return this.httpClient.get<AccountCategory[]>(`${this.baseUrl}/accounts/categories`);
  }

  public getAccounts(userId: string, criteria: AccountCriteria = {}): Observable<Account[]> {
    const params = this.buildParams(criteria);
    return this.httpClient.get<Account[]>(`${this.baseUrl}/accounts/users/${userId}`, {params});
  }

  private buildParams(criteria: AccountCriteria): HttpParams {
    let params = new HttpParams();

    function recurse(obj: any, prefix: string = '') {
      Object.entries(obj).forEach(([key, value]) => {
        if (value == null) return;
        const paramName = prefix
          ? `${prefix}.${key}`
          : key;

        if (typeof value === 'object' && !Array.isArray(value)) {
          recurse(value, paramName);
        } else {
          params = params.set(paramName, value.toString());
        }
      });
    }

    recurse(criteria);
    return params;
  }

  public createAccount(request: AccountRequest): Observable<Account> {
    return this.httpClient.post<Account>(`${this.baseUrl}/accounts`, request);
  }

  public updateAccount(accountId: string, request: AccountRequest): Observable<Account> {
    return this.httpClient.put<Account>(`${this.baseUrl}/accounts/${accountId}`, request);
  }

  public deleteAccount(accountId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/accounts/${accountId}`);
  }
}
