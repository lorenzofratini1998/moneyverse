import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {EnrichedTransaction, Tag, TagRequest, Transaction, TransactionRequest} from './transaction.model';
import {Category} from '../category/category.model';
import {Account} from '../account/account.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = environment.services.transactionManagementUrl

  public getTransactionsByUser(userId: string): Observable<Transaction[]> {
    return this.httpClient.get<Transaction[]>(`${this.baseUrl}/transactions/users/${userId}`);
  }

  public createTransaction(request: TransactionRequest): Observable<Transaction> {
    return this.httpClient.post<Transaction>(`${this.baseUrl}/transactions`, request);
  }

  enrichTransaction(
    transaction: Transaction,
    accounts: Account[],
    categories: Category[]
  ): EnrichedTransaction {
    const account = accounts.find(a => a.accountId === transaction.accountId)!;
    const category = categories.find(c => c.categoryId === transaction.categoryId)!;
    return {...transaction, account, category};
  }

  createTag(request: TagRequest): Observable<Tag> {
    return this.httpClient.post<Tag>(`${this.baseUrl}/tags`, request);
  }

  getTagsByUser(userId: string): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(`${this.baseUrl}/tags/users/${userId}`);
  }

  getTag(tagId: string): Observable<Tag> {
    return this.httpClient.get<Tag>(`${this.baseUrl}/tags/${tagId}`);
  }

  updateTag(tagId: string, request: TagRequest): Observable<Tag> {
    return this.httpClient.put<Tag>(`${this.baseUrl}/tags/${tagId}`, request);
  }

  deleteTag(tagId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/tags/${tagId}`);
  }
}
