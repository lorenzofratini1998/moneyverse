import {inject, Injectable} from '@angular/core';
import {BehaviorSubject, filter, Observable, shareReplay, Subject} from 'rxjs';
import {AuthService} from '../../core/auth/auth.service';
import {fetchEventSource} from '@microsoft/fetch-event-source';

export interface SSEEvent {
  type: string;
  data: string;
  event?: string;
}

interface SseConnection {
  stream: Observable<SSEEvent>;
  abortController: AbortController;
}

@Injectable({
  providedIn: 'root'
})
export class SseService {

  private readonly authService = inject(AuthService);
  private connectionPool = new Map<string, SseConnection>();

  getStream(url: string, params?: Record<string, string>): Observable<SSEEvent> {
    const finalUrl = params ? `${url}?${new URLSearchParams(params).toString()}` : url;

    const existingConnection = this.connectionPool.get(finalUrl);
    if (existingConnection) {
      return existingConnection.stream;
    }

    const abortController = new AbortController();
    const self = this;

    const newStream = new Observable<SSEEvent>(subscriber => {

      fetchEventSource(finalUrl, {
        method: 'GET',
        headers: {
          'Accept': 'text/event-stream',
          'Authorization': `Bearer ${this.authService.token}`,
          'Cache-Control': 'no-cache'
        },
        signal: abortController.signal,

        async onopen(response) {
          if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
            return;
          }
          const errorMsg = `SSE Error: ${response.status} - ${response.statusText}`;
          console.error(errorMsg);
          subscriber.error(new Error(errorMsg));
        },

        onmessage(event) {
          subscriber.next({
            type: event.event || 'message',
            data: event.data,
            event: event.event
          });
        },

        onclose() {
          subscriber.complete();
          self.connectionPool.delete(finalUrl);
        },

        onerror(err) {
          subscriber.error(err);
          self.connectionPool.delete(finalUrl);
        }
      }).catch(error => {
        subscriber.error(error);
        self.connectionPool.delete(finalUrl);
      });
    });

    const sharedStream = newStream.pipe(
      shareReplay(1)
    );

    this.connectionPool.set(finalUrl, {
      stream: sharedStream,
      abortController: abortController
    });

    return sharedStream;
  }

  disconnectStream(url: string, params?: Record<string, string>): void {
    const finalUrl = params ? `${url}?${new URLSearchParams(params).toString()}` : url;
    const connection = this.connectionPool.get(finalUrl);

    if (connection) {
      connection.abortController.abort();
      this.connectionPool.delete(finalUrl);
    }
  }

  disconnectAll(): void {
    this.connectionPool.forEach((connection, url) => {
      connection.abortController.abort();
    });
    this.connectionPool.clear();
  }

}


