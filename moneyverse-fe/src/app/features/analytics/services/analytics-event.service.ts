import {inject, Injectable, signal} from '@angular/core';
import {SSEEvent, SseService} from '../../../shared/services/sse.service';
import {AuthService} from '../../../core/auth/auth.service';
import {filter, map, Observable, tap} from 'rxjs';
import {toObservable} from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsEventService {
  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private stream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/analytics/sse';
  private readonly reloadTrigger = signal(0);

  constructor() {
    this.getStream().subscribe();
  }

  private getStream(): Observable<SSEEvent> {
    if (!this.stream$) {
      this.stream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.user().userId
      }).pipe(
        tap(event => {
          if (event.type === "TRANSACTION_EVENTS_UPDATED") {
            this.reloadTrigger.update(v => v + 1);
          }
        })
      );
    }
    return this.stream$;
  }

  reload$ = toObservable(this.reloadTrigger.asReadonly());
}
