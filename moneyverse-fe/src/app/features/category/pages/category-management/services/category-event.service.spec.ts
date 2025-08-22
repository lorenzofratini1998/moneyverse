import {TestBed} from '@angular/core/testing';

import {CategoryEventService} from './category-event.service';

describe('CategoryEventService', () => {
  let service: CategoryEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoryEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
