import {TestBed} from '@angular/core/testing';

import {AccountDistributionChartService} from './account-distribution-chart.service';

describe('AccountDistributionService', () => {
  let service: AccountDistributionChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountDistributionChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
