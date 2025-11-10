import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {filter, map, Observable} from 'rxjs';
import {Budget} from '../../../category.model';
import {BudgetSseEventEnum} from '../models/events.models';

@Injectable({
  providedIn: 'root'
})
export class BudgetEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);

  private budgetStream$: Observable<SSEEvent> | null = null;
  private readonly URL = '/budgets/sse';

  private getStream(): Observable<SSEEvent> {
    if (!this.budgetStream$) {
      this.budgetStream$ = this.sseService.getStream(this.URL, {
        userId: this.authService.authenticatedUser.userId
      });
    }
    return this.budgetStream$;
  }

  onBudgetCreated(): Observable<Budget> {
    return this.getStream().pipe(
      filter(event => event.type === BudgetSseEventEnum.BUDGET_CREATED),
      map(event => JSON.parse(event.data) as Budget)
    );
  }

  onBudgetUpdated(): Observable<Budget> {
    return this.getStream().pipe(
      filter(event => event.type === BudgetSseEventEnum.BUDGET_UPDATED),
      map(event => JSON.parse(event.data) as Budget)
    );
  }

  onBudgetDeleted(): Observable<string> {
    return this.getStream().pipe(
      filter(event => event.type === BudgetSseEventEnum.BUDGET_DELETED),
      map(event => JSON.parse(event.data) as string)
    );
  }

}
