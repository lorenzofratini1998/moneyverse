import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionAnalyticsTransactionsTableComponent} from './transaction-analytics-transactions-table.component';

describe('TransactionAnalyticsTransactionsTableComponent', () => {
  let component: TransactionAnalyticsTransactionsTableComponent;
  let fixture: ComponentFixture<TransactionAnalyticsTransactionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionAnalyticsTransactionsTableComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionAnalyticsTransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
