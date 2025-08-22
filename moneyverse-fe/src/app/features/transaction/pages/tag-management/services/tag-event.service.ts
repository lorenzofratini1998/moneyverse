import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {map, Observable} from 'rxjs';
import {Tag} from '../../../transaction.model';
import {TagSseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class TagEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.services.transactionManagementUrl;

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`${this.baseUrl}/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onTagCreated(): Observable<Tag> {
    return this.sseService.addEventListener(TagSseEventEnum.TAG_CREATED).pipe(
      map(event => JSON.parse(event.data) as Tag)
    )
  }

  onTagUpdated(): Observable<Tag> {
    return this.sseService.addEventListener(TagSseEventEnum.TAG_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Tag)
    )
  }

  onTagDeleted(): Observable<Tag> {
    return this.sseService.addEventListener(TagSseEventEnum.TAG_DELETED).pipe(
      map(event => JSON.parse(event.data) as Tag)
    )
  }

  disconnect(): void {
    this.sseService.disconnect();
  }

}
