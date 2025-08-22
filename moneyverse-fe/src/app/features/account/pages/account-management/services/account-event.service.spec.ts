import {TestBed} from '@angular/core/testing';

import {AccountEventService} from './account-event.service';

describe('AccountEventService', () => {
  let service: AccountEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
