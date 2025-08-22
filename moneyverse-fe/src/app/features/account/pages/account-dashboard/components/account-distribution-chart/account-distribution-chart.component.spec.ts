import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountDistributionChartComponent} from './account-distribution-chart.component';

describe('AccountPieChartComponent', () => {
  let component: AccountDistributionChartComponent;
  let fixture: ComponentFixture<AccountDistributionChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountDistributionChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountDistributionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
