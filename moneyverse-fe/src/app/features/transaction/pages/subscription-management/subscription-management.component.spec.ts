import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SubscriptionManagementComponent} from './subscription-management.component';

describe('SubscriptionManagementComponent', () => {
  let component: SubscriptionManagementComponent;
  let fixture: ComponentFixture<SubscriptionManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionManagementComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SubscriptionManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
