import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountTransactionsTableComponent} from './account-transactions-table.component';

describe('AccountTransactionsTableComponent', () => {
  let component: AccountTransactionsTableComponent;
  let fixture: ComponentFixture<AccountTransactionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountTransactionsTableComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountTransactionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
