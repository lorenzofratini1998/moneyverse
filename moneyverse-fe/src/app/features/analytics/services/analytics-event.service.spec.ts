import { TestBed } from '@angular/core/testing';

import { AnalyticsEventService } from './analytics-event.service';

describe('AnalyticsEventService', () => {
  let service: AnalyticsEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnalyticsEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
