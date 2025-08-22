import {inject, Injectable} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {environment} from "../../../../../../environments/environment";
import {map, Observable} from 'rxjs';
import {Subscription} from '../../../transaction.model';
import {SubscriptionSseEventEnum} from '../models/events.models';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.services.transactionManagementUrl;

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`${this.baseUrl}/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onSubscriptionCreated(): Observable<Subscription> {
    return this.sseService.addEventListener(SubscriptionSseEventEnum.SUBSCRIPTION_CREATED).pipe(
      map(event => JSON.parse(event.data) as Subscription)
    );
  }

  onSubscriptionUpdated(): Observable<Subscription> {
    return this.sseService.addEventListener(SubscriptionSseEventEnum.SUBSCRIPTION_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Subscription)
    );
  }

  onSubscriptionDeleted(): Observable<Subscription> {
    return this.sseService.addEventListener(SubscriptionSseEventEnum.SUBSCRIPTION_DELETED).pipe(
      map(event => JSON.parse(event.data) as Subscription)
    );
  }

  disconnect(): void {
    this.sseService.disconnect();
  }

}
