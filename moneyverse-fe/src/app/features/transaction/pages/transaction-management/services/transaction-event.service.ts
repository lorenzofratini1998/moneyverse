import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {filter, map, Observable} from 'rxjs';
import {Transaction, Transfer} from '../../../transaction.model';
import {TransactionSseEventEnum, TransferSseEventEnum} from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private transactionStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/transactions/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.transactionStream$) {
      this.transactionStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.user().userId
      });
    }
    return this.transactionStream$;
  }

  onTransactionCreated(): Observable<Transaction> {
    return this.getStream().pipe(
      filter(event => event.type === TransactionSseEventEnum.TRANSACTION_CREATED),
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransactionUpdated(): Observable<Transaction> {
    return this.getStream().pipe(
      filter(event => event.type === TransactionSseEventEnum.TRANSACTION_UPDATED),
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransactionDeleted(): Observable<Transaction> {
    return this.getStream().pipe(
      filter(event => event.type === TransactionSseEventEnum.TRANSACTION_DELETED),
      map(event => JSON.parse(event.data) as Transaction)
    )
  }

  onTransferCreated(): Observable<Transfer> {
    return this.getStream().pipe(
      filter(event => event.type === TransferSseEventEnum.TRANSFER_CREATED),
      map(event => JSON.parse(event.data) as Transfer)
    )
  }

  onTransferUpdated(): Observable<Transfer> {
    return this.getStream().pipe(
      filter(event => event.type === TransferSseEventEnum.TRANSFER_UPDATED),
      map(event => JSON.parse(event.data) as Transfer)
    )
  }

  onTransferDeleted(): Observable<Transfer> {
    return this.getStream().pipe(
      filter(event => event.type === TransferSseEventEnum.TRANSFER_DELETED),
      map(event => JSON.parse(event.data) as Transfer)
    )
  }
}
