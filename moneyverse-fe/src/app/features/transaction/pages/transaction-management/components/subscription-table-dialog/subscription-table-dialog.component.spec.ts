import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SubscriptionTableDialogComponent} from './subscription-table-dialog.component';

describe('SubscriptionTableDialogComponent', () => {
  let component: SubscriptionTableDialogComponent;
  let fixture: ComponentFixture<SubscriptionTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionTableDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SubscriptionTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
