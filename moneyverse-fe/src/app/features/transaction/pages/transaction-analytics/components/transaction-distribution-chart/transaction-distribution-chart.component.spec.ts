import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionDistributionChartComponent} from './transaction-distribution-chart.component';

describe('TransactionDistributionChartComponent', () => {
  let component: TransactionDistributionChartComponent;
  let fixture: ComponentFixture<TransactionDistributionChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionDistributionChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionDistributionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
