import {TestBed} from '@angular/core/testing';

import {TransactionDistributionChartService} from './transaction-distribution-chart.service';

describe('TransactionDistributionChartService', () => {
  let service: TransactionDistributionChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionDistributionChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
