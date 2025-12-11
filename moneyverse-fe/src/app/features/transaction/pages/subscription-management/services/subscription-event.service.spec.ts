import {TestBed} from '@angular/core/testing';

import {SubscriptionEventService} from './subscription-event.service';

describe('SubscriptionEventService', () => {
  let service: SubscriptionEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubscriptionEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
