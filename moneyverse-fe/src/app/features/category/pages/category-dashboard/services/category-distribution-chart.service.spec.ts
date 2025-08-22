import {TestBed} from '@angular/core/testing';

import {CategoryDistributionChartService} from './category-distribution-chart.service';

describe('CategoryDistributionChartService', () => {
  let service: CategoryDistributionChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoryDistributionChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
