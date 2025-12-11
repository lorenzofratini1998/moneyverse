import {ComponentFixture, TestBed} from '@angular/core/testing';

import {
  CategoryAnalyticsTransactionTableDialogComponent
} from './category-analytics-transaction-table-dialog.component';

describe('CategoryAnalyticsTransactionTableDialogComponent', () => {
  let component: CategoryAnalyticsTransactionTableDialogComponent;
  let fixture: ComponentFixture<CategoryAnalyticsTransactionTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryAnalyticsTransactionTableDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryAnalyticsTransactionTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
