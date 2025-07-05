import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BadgeFilterComponent} from './badge-filter.component';

describe('BadgeFilterComponent', () => {
  let component: BadgeFilterComponent;
  let fixture: ComponentFixture<BadgeFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BadgeFilterComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(BadgeFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
