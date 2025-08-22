import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BudgetTransactionsTableComponent} from './budget-transactions-table.component';

describe('BudgetTransactionsTableComponent', () => {
  let component: BudgetTransactionsTableComponent;
  let fixture: ComponentFixture<BudgetTransactionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetTransactionsTableComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(BudgetTransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
