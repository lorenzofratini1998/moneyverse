import {TestBed} from '@angular/core/testing';

import {SubscriptionTableService} from './subscription-table.service';

describe('SubscriptionTableService', () => {
  let service: SubscriptionTableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubscriptionTableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
