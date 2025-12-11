import {TestBed} from '@angular/core/testing';

import {TransactionKpiService} from './transaction-kpi.service';

describe('TransactionKpiService', () => {
  let service: TransactionKpiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionKpiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
