import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountPieChartComponent} from './account-pie-chart.component';

describe('AccountPieChartComponent', () => {
  let component: AccountPieChartComponent;
  let fixture: ComponentFixture<AccountPieChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountPieChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountPieChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
