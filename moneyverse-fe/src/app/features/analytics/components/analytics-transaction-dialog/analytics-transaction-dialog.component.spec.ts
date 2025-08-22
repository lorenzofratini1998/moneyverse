import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AnalyticsTransactionDialogComponent} from './analytics-transaction-dialog.component';

describe('AnalyticsTransactionDialogComponent', () => {
  let component: AnalyticsTransactionDialogComponent;
  let fixture: ComponentFixture<AnalyticsTransactionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalyticsTransactionDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AnalyticsTransactionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
