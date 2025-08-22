import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {environment} from '../../../../../../environments/environment';
import {map, Observable} from 'rxjs';
import {AuthService} from '../../../../../core/auth/auth.service';
import {Account} from '../../../account.model';
import {AccountSseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class AccountEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.services.accountManagementUrl

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`${this.baseUrl}/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onAccountCreated(): Observable<Account> {
    return this.sseService.addEventListener(AccountSseEventEnum.ACCOUNT_CREATED).pipe(
      map(e => JSON.parse(e.data) as Account)
    )
  }

  onAccountUpdated(): Observable<Account> {
    return this.sseService.addEventListener(AccountSseEventEnum.ACCOUNT_UPDATED).pipe(
      map(e => JSON.parse(e.data) as Account)
    )
  }

  onAccountDeleted(): Observable<string> {
    return this.sseService.addEventListener(AccountSseEventEnum.ACCOUNT_DELETED).pipe(
      map(e => JSON.parse(e.data) as string)
    )
  }

  disconnect(): void {
    this.sseService.disconnect();
  }
}
