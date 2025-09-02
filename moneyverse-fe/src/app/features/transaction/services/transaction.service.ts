import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {
  Subscription,
  SubscriptionRequest,
  Tag,
  TagRequest,
  Transaction,
  TransactionCriteria,
  TransactionRequest,
  TransactionRequestItem,
  Transfer,
  TransferRequest
} from '../transaction.model';
import {PageResponse} from '../../../shared/models/common.model';
import {buildHttpParams} from '../../../shared/utils/utils';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = environment.services.krakendUrl

  public getTransactionsByUser(userId: string, criteria: TransactionCriteria = {}): Observable<PageResponse<Transaction>> {
    const params = buildHttpParams(criteria);
    return this.httpClient.get<PageResponse<Transaction>>(`${this.baseUrl}/transactions/users/${userId}`, {params});
  }

  public createTransaction(request: TransactionRequest): Observable<Transaction> {
    return this.httpClient.post<Transaction>(`${this.baseUrl}/transactions`, request);
  }

  public updateTransaction(transactionId: string, request: TransactionRequestItem): Observable<Transaction> {
    return this.httpClient.put<Transaction>(`${this.baseUrl}/transactions/${transactionId}`, request);
  }

  public deleteTransaction(transactionId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/transactions/${transactionId}`);
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

  createTransfer(request: TransferRequest): Observable<Transfer> {
    return this.httpClient.post<Transfer>(`${this.baseUrl}/transfers`, request);
  }

  updateTransfer(transferId: string, request: TransferRequest): Observable<Transfer> {
    return this.httpClient.put<Transfer>(`${this.baseUrl}/transfers/${transferId}`, request);
  }

  deleteTransfer(transferId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/transfers/${transferId}`);
  }

  getTransactionsByTransferId(transferId: string): Observable<Transfer> {
    return this.httpClient.get<Transfer>(`${this.baseUrl}/transfers/${transferId}`);
  }

  createSubscription(request: SubscriptionRequest): Observable<Subscription> {
    return this.httpClient.post<Subscription>(`${this.baseUrl}/subscriptions`, request);
  }

  getSubscription(subscriptionId: string): Observable<Subscription> {
    return this.httpClient.get<Subscription>(`${this.baseUrl}/subscriptions/${subscriptionId}`);
  }

  getSubscriptionsByUser(userId: string): Observable<Subscription[]> {
    return this.httpClient.get<Subscription[]>(`${this.baseUrl}/subscriptions/users/${userId}`);
  }

  deleteSubscription(subscriptionId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/subscriptions/${subscriptionId}`);
  }

  updateSubscription(subscriptionId: string, request: SubscriptionRequest): Observable<Subscription> {
    return this.httpClient.put<Subscription>(`${this.baseUrl}/subscriptions/${subscriptionId}`, request);
  }
}
