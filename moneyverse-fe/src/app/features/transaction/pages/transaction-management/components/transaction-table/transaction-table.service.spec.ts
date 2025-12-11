import {TestBed} from '@angular/core/testing';

import {TransactionTableService} from './transaction-table.service';

describe('TransactionTableService', () => {
  let service: TransactionTableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionTableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
