import {TestBed} from '@angular/core/testing';

import {CategoryTrendChartService} from './category-trend-chart.service';

describe('CategoryTrendChartService', () => {
  let service: CategoryTrendChartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoryTrendChartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
