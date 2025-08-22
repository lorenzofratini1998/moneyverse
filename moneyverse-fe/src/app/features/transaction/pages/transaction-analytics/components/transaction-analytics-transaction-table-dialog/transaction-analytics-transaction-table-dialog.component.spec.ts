import {ComponentFixture, TestBed} from '@angular/core/testing';

import {
  TransactionAnalyticsTransactionTableDialogComponent
} from './transaction-analytics-transaction-table-dialog.component';

describe('TransactionAnalyticsTransactionTableDialogComponent', () => {
  let component: TransactionAnalyticsTransactionTableDialogComponent;
  let fixture: ComponentFixture<TransactionAnalyticsTransactionTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionAnalyticsTransactionTableDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionAnalyticsTransactionTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
