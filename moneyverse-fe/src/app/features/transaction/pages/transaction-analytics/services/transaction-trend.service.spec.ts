import {TestBed} from '@angular/core/testing';

import {TransactionTrendService} from './transaction-trend.service';

describe('TransactionTrendService', () => {
  let service: TransactionTrendService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionTrendService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
