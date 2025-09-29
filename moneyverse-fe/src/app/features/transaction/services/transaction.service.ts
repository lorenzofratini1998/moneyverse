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

  public getTransactionsByUser(userId: string, criteria: TransactionCriteria = {}): Observable<PageResponse<Transaction>> {
    const params = buildHttpParams(criteria);
    return this.httpClient.get<PageResponse<Transaction>>(`/transactions/users/${userId}`, {params});
  }

  public createTransaction(request: TransactionRequest): Observable<Transaction> {
    return this.httpClient.post<Transaction>(`/transactions`, request);
  }

  public updateTransaction(transactionId: string, request: TransactionRequestItem): Observable<Transaction> {
    return this.httpClient.put<Transaction>(`/transactions/${transactionId}`, request);
  }

  public deleteTransaction(transactionId: string): Observable<void> {
    return this.httpClient.delete<void>(`/transactions/${transactionId}`);
  }

  createTag(request: TagRequest): Observable<Tag> {
    return this.httpClient.post<Tag>(`/tags`, request);
  }

  getTagsByUser(userId: string): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(`/tags/users/${userId}`);
  }

  getTag(tagId: string): Observable<Tag> {
    return this.httpClient.get<Tag>(`/tags/${tagId}`);
  }

  updateTag(tagId: string, request: TagRequest): Observable<Tag> {
    return this.httpClient.put<Tag>(`/tags/${tagId}`, request);
  }

  deleteTag(tagId: string): Observable<void> {
    return this.httpClient.delete<void>(`/tags/${tagId}`);
  }

  createTransfer(request: TransferRequest): Observable<Transfer> {
    return this.httpClient.post<Transfer>(`/transfers`, request);
  }

  updateTransfer(transferId: string, request: TransferRequest): Observable<Transfer> {
    return this.httpClient.put<Transfer>(`/transfers/${transferId}`, request);
  }

  deleteTransfer(transferId: string): Observable<void> {
    return this.httpClient.delete<void>(`/transfers/${transferId}`);
  }

  getTransactionsByTransferId(transferId: string): Observable<Transfer> {
    return this.httpClient.get<Transfer>(`/transfers/${transferId}`);
  }

  createSubscription(request: SubscriptionRequest): Observable<Subscription> {
    return this.httpClient.post<Subscription>(`/subscriptions`, request);
  }

  getSubscription(subscriptionId: string): Observable<Subscription> {
    return this.httpClient.get<Subscription>(`/subscriptions/${subscriptionId}`);
  }

  getSubscriptionsByUser(userId: string): Observable<Subscription[]> {
    return this.httpClient.get<Subscription[]>(`/subscriptions/users/${userId}`);
  }

  deleteSubscription(subscriptionId: string): Observable<void> {
    return this.httpClient.delete<void>(`/subscriptions/${subscriptionId}`);
  }

  updateSubscription(subscriptionId: string, request: SubscriptionRequest): Observable<Subscription> {
    return this.httpClient.put<Subscription>(`/subscriptions/${subscriptionId}`, request);
  }
}
