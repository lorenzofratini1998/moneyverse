import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionTrendChartComponent} from './transaction-trend-chart.component';

describe('TransactionTrendChartComponent', () => {
  let component: TransactionTrendChartComponent;
  let fixture: ComponentFixture<TransactionTrendChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionTrendChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionTrendChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
