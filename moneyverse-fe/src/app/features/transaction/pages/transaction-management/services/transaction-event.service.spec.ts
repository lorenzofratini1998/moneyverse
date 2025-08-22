import {TestBed} from '@angular/core/testing';

import {TransactionEventService} from './transaction-event.service';

describe('TransactionEventService', () => {
  let service: TransactionEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
