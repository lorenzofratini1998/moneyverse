import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {map, Observable} from 'rxjs';
import {Category} from '../../../category.model';
import {CategorySseEventEnum} from '../models/events.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.services.nginxUrl;

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`${this.baseUrl}/budgets/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onCategoryCreated(): Observable<Category> {
    return this.sseService.addEventListener(CategorySseEventEnum.CATEGORY_CREATED).pipe(
      map(event => JSON.parse(event.data) as Category)
    )
  }

  onCategoryUpdated(): Observable<Category> {
    return this.sseService.addEventListener(CategorySseEventEnum.CATEGORY_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Category)
    )
  }

  onCategoryDeleted(): Observable<string> {
    return this.sseService.addEventListener(CategorySseEventEnum.CATEGORY_DELETED).pipe(
      map(event => JSON.parse(event.data) as string)
    )
  }

  disconnect(): void {
    this.sseService.disconnect();
  }
}
