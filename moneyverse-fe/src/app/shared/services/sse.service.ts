import {Injectable} from '@angular/core';
import {BehaviorSubject, filter, Observable, Subject} from 'rxjs';
import {AuthService} from '../../core/auth/auth.service';
import {fetchEventSource} from '@microsoft/fetch-event-source';

export interface SSEEvent {
  type: string;
  data: string;
  event?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SseService {

  private eventSubject = new Subject<SSEEvent>();
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);
  private abortController: AbortController | null = null;

  constructor(private authService: AuthService) {
  }

  connect(url: string, params?: Record<string, string>): Observable<SSEEvent> {
    if (this.connectionStatusSubject.value) {
      return this.eventSubject.asObservable();
    }

    const finalUrl = params ? `${url}?${new URLSearchParams(params).toString()}` : url;

    this.abortController = new AbortController();

    const self = this;

    fetchEventSource(finalUrl, {
      method: 'GET',
      headers: {
        'Accept': 'text/event-stream',
        'Authorization': `Bearer ${this.authService.token}`,
        'Cache-Control': 'no-cache'
      },
      signal: this.abortController.signal,

      async onopen(response) {
        if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
          self.connectionStatusSubject.next(true);
          return;
        } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
          self.connectionStatusSubject.next(false);
          throw new Error(`Client error: ${response.status} - ${response.statusText}`);
        } else {
          self.connectionStatusSubject.next(false);
          throw new Error(`Server error: ${response.status} - ${response.statusText}`);
        }
      },

      onmessage(event) {
        self.eventSubject.next({
          type: event.event || 'message',
          data: event.data,
          event: event.event
        });
      },

      onclose() {
        self.connectionStatusSubject.next(false);
      },

      onerror(err) {
        console.error('SSE connection error:', err);
        self.connectionStatusSubject.next(false);
        self.eventSubject.error(err);
      }
    }).catch(error => {
      console.error('Failed to establish SSE connection:', error);
      this.connectionStatusSubject.next(false);
      this.eventSubject.error(error);
    });

    return this.eventSubject.asObservable();
  }

  disconnect(): void {
    if (this.abortController) {
      this.abortController.abort();
      this.abortController = null;
    }
    this.connectionStatusSubject.next(false);
  }

  addEventListener(eventType: string): Observable<SSEEvent> {
    return this.eventSubject.asObservable().pipe(
      filter(event => event.type === eventType || event.event === eventType)
    );
  }

  /*isConnectionActive(): boolean {
    return this.connectionStatusSubject.value;
  }

  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatusSubject.asObservable();
  }

  reconnect(): Observable<SSEEvent> {
    this.disconnect();
    return this.connect();
  }*/
}


