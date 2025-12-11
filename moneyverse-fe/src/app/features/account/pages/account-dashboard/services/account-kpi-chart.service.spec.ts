import {TestBed} from '@angular/core/testing';

import {AccountKpiChartService} from './account-kpi-chart.service';

describe('AccountKpiService', () => {
  let service: AccountKpiChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountKpiChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
