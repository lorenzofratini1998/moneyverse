import {inject, Injectable} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {filter, map, Observable} from 'rxjs';
import {SubscriptionTransaction} from '../../../transaction.model';
import {SubscriptionSseEventEnum} from '../models/events.models';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private subscriptionStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/transactions/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.subscriptionStream$) {
      this.subscriptionStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.authenticatedUser.userId
      });
    }
    return this.subscriptionStream$;
  }

  onSubscriptionCreated(): Observable<SubscriptionTransaction> {
    return this.getStream().pipe(
      filter(event => event.type === SubscriptionSseEventEnum.SUBSCRIPTION_CREATED),
      map(event => JSON.parse(event.data) as SubscriptionTransaction)
    )
  }

  onSubscriptionUpdated(): Observable<SubscriptionTransaction> {
    return this.getStream().pipe(
      filter(event => event.type === SubscriptionSseEventEnum.SUBSCRIPTION_UPDATED),
      map(event => JSON.parse(event.data) as SubscriptionTransaction)
    )
  }

  onSubscriptionDeleted(): Observable<SubscriptionTransaction> {
    return this.getStream().pipe(
      filter(event => event.type === SubscriptionSseEventEnum.SUBSCRIPTION_DELETED),
      map(event => JSON.parse(event.data) as SubscriptionTransaction)
    )
  }

}
