import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {environment} from '../../../../../../environments/environment';
import {filter, map, Observable} from 'rxjs';
import {AuthService} from '../../../../../core/auth/auth.service';
import {Account} from '../../../account.model';
import {AccountSseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class AccountEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private accountStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/accounts/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.accountStream$) {
      this.accountStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.authenticatedUser.userId
      });
    }
    return this.accountStream$;
  }

  onAccountCreated(): Observable<Account> {
    return this.getStream().pipe(
      filter(event => event.type === AccountSseEventEnum.ACCOUNT_CREATED),
      map(event => JSON.parse(event.data) as Account)
    )
  }

  onAccountUpdated(): Observable<Account> {
    return this.getStream().pipe(
      filter(event => event.type === AccountSseEventEnum.ACCOUNT_UPDATED),
      map(event => JSON.parse(event.data) as Account)
    )
  }

  onAccountDeleted(): Observable<string> {
    return this.getStream().pipe(
      filter(event => event.type === AccountSseEventEnum.ACCOUNT_DELETED),
      map(event => JSON.parse(event.data) as string)
    )
  }
}
