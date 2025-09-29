import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Account, AccountCategory, AccountCriteria, AccountRequest} from '../account.model';
import {Observable} from 'rxjs';
import {buildHttpParams} from '../../../shared/utils/utils';


@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly httpClient = inject(HttpClient);

  public getAccountCategories(): Observable<AccountCategory[]> {
    return this.httpClient.get<AccountCategory[]>(`/accounts/categories`);
  }

  public getAccounts(userId: string, criteria: AccountCriteria = {}): Observable<Account[]> {
    const params = buildHttpParams(criteria);
    return this.httpClient.get<Account[]>(`/accounts/users/${userId}`, {params});
  }

  public createAccount(request: AccountRequest): Observable<Account> {
    return this.httpClient.post<Account>(`/accounts`, request);
  }

  public updateAccount(accountId: string, request: AccountRequest): Observable<Account> {
    return this.httpClient.put<Account>(`/accounts/${accountId}`, request);
  }

  public deleteAccount(accountId: string): Observable<void> {
    return this.httpClient.delete<void>(`/accounts/${accountId}`);
  }
}
