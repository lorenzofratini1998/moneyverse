import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryAnalyticsTransactionsTableComponent} from './category-analytics-transactions-table.component';

describe('CategoryAnalyticsTransactionsTableComponent', () => {
  let component: CategoryAnalyticsTransactionsTableComponent;
  let fixture: ComponentFixture<CategoryAnalyticsTransactionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryAnalyticsTransactionsTableComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryAnalyticsTransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
