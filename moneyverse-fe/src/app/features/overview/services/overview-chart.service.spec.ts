import { TestBed } from '@angular/core/testing';

import { OverviewChartService } from './overview-chart.service';

describe('OverviewChartService', () => {
  let service: OverviewChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OverviewChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
