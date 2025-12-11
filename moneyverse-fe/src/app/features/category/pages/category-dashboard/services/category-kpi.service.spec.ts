import {TestBed} from '@angular/core/testing';

import {CategoryKpiService} from './category-kpi.service';

describe('CategoryKpiService', () => {
  let service: CategoryKpiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoryKpiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
