import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {filter, map, Observable} from 'rxjs';
import {Category} from '../../../category.model';
import {CategorySseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private categoryStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/budgets/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.categoryStream$) {
      this.categoryStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.user().userId
      });
    }
    return this.categoryStream$;
  }

  onCategoryCreated(): Observable<Category> {
    return this.getStream().pipe(
      filter(event => event.type === CategorySseEventEnum.CATEGORY_CREATED),
      map(event => JSON.parse(event.data) as Category)
    );
  }

  onCategoryUpdated(): Observable<Category> {
    return this.getStream().pipe(
      filter(event => event.type === CategorySseEventEnum.CATEGORY_UPDATED),
      map(event => JSON.parse(event.data) as Category)
    );
  }

  onCategoryDeleted(): Observable<string> {
    return this.getStream().pipe(
      filter(event => event.type === CategorySseEventEnum.CATEGORY_DELETED),
      map(event => JSON.parse(event.data) as string)
    );
  }
}
