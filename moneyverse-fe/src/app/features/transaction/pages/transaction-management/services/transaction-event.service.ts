import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {map, Observable} from 'rxjs';
import {Transaction, Transfer} from '../../../transaction.model';
import {TransactionSseEventEnum, TransferSseEventEnum} from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`/transactions/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onTransactionCreated(): Observable<Transaction> {
    return this.sseService.addEventListener(TransactionSseEventEnum.TRANSACTION_CREATED).pipe(
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransactionUpdated(): Observable<Transaction> {
    return this.sseService.addEventListener(TransactionSseEventEnum.TRANSACTION_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransactionDeleted(): Observable<Transaction> {
    return this.sseService.addEventListener(TransactionSseEventEnum.TRANSACTION_DELETED).pipe(
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransferCreated(): Observable<Transfer> {
    return this.sseService.addEventListener(TransferSseEventEnum.TRANSFER_CREATED).pipe(
      map(event => JSON.parse(event.data) as Transfer)
    )
  }

  onTransferUpdated(): Observable<Transfer> {
    return this.sseService.addEventListener(TransferSseEventEnum.TRANSFER_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Transfer)
    )
  }

  onTransferDeleted(): Observable<Transfer> {
    return this.sseService.addEventListener(TransferSseEventEnum.TRANSFER_DELETED).pipe(
      map(event => JSON.parse(event.data) as Transfer)
    )
  }

  disconnect(): void {
    this.sseService.disconnect();
  }
}
