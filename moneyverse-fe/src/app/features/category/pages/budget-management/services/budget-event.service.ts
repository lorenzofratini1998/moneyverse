import {inject, Injectable} from '@angular/core';
import {SSEEvent, SseService} from '../../../../../shared/services/sse.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {environment} from '../../../../../../environments/environment';
import {map, Observable} from 'rxjs';
import {Budget} from '../../../category.model';
import {BudgetSseEventEnum} from '../models/events.models';

@Injectable({
  providedIn: 'root'
})
export class BudgetEventService {

  private readonly sseService = inject(SseService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.services.nginxUrl;

  connect(): Observable<SSEEvent> {
    return this.sseService.connect(`${this.baseUrl}/budgets/sse`, {userId: this.authService.authenticatedUser.userId})
  }

  onBudgetCreated(): Observable<Budget> {
    return this.sseService.addEventListener(BudgetSseEventEnum.BUDGET_CREATED).pipe(
      map(event => JSON.parse(event.data) as Budget)
    )
  }

  onBudgetUpdated(): Observable<Budget> {
    return this.sseService.addEventListener(BudgetSseEventEnum.BUDGET_UPDATED).pipe(
      map(event => JSON.parse(event.data) as Budget)
    )
  }

  onBudgetDeleted(): Observable<string> {
    return this.sseService.addEventListener(BudgetSseEventEnum.BUDGET_DELETED).pipe(
      map(event => JSON.parse(event.data) as string)
    )
  }

  disconnect(): void {
    this.sseService.disconnect();
  }
}
