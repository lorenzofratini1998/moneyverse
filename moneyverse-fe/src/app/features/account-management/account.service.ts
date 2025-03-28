import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Account, AccountCategory, AccountRequest} from './account.model';
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

  public getAccounts(userId: string): Observable<Account[]> {
    return this.httpClient.get<Account[]>(`${this.baseUrl}/accounts/users/${userId}`);
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
