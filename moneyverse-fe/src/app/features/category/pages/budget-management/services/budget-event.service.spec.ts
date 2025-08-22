import {TestBed} from '@angular/core/testing';

import {BudgetEventService} from './budget-event.service';

describe('BudgetEventService', () => {
  let service: BudgetEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BudgetEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
