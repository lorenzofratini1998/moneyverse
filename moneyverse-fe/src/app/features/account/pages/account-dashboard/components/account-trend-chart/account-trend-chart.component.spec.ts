import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountTrendChartComponent} from './account-trend-chart.component';

describe('AccountTrendComponent', () => {
  let component: AccountTrendChartComponent;
  let fixture: ComponentFixture<AccountTrendChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountTrendChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountTrendChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
