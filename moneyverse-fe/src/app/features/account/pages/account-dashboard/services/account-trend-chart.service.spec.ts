import {TestBed} from '@angular/core/testing';

import {AccountTrendChartService} from './account-trend-chart.service';

describe('AccountTrendService', () => {
  let service: AccountTrendChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountTrendChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
