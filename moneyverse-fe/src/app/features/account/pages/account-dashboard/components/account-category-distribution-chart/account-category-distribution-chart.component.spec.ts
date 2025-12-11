import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountCategoryDistributionChartComponent} from './account-category-distribution-chart.component';

describe('AccountCategoryPieChartComponent', () => {
  let component: AccountCategoryDistributionChartComponent;
  let fixture: ComponentFixture<AccountCategoryDistributionChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountCategoryDistributionChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountCategoryDistributionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
