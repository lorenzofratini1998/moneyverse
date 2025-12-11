import {TestBed} from '@angular/core/testing';

import {TagEventService} from './tag-event.service';

describe('TagEventService', () => {
  let service: TagEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TagEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
