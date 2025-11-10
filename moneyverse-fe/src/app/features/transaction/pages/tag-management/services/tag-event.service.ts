import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {filter, map, Observable} from 'rxjs';
import {Tag} from '../../../transaction.model';
import {TagSseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class TagEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private tagStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/transactions/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.tagStream$) {
      this.tagStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.authenticatedUser.userId
      });
    }
    return this.tagStream$;
  }

  onTagCreated(): Observable<Tag> {
    return this.getStream().pipe(
      filter(event => event.type === TagSseEventEnum.TAG_CREATED),
      map(event => JSON.parse(event.data) as Tag)
    )
  }

  onTagUpdated(): Observable<Tag> {
    return this.getStream().pipe(
      filter(event => event.type === TagSseEventEnum.TAG_UPDATED),
      map(event => JSON.parse(event.data) as Tag)
    )
  }

  onTagDeleted(): Observable<Tag> {
    return this.getStream().pipe(
      filter(event => event.type === TagSseEventEnum.TAG_DELETED),
      map(event => JSON.parse(event.data) as Tag)
    )
  }

}
