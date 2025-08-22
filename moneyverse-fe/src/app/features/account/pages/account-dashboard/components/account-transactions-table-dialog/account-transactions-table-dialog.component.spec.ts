import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountTransactionsTableDialogComponent} from './account-transactions-table-dialog.component';

describe('AccountTransactionsTableDialogComponent', () => {
  let component: AccountTransactionsTableDialogComponent;
  let fixture: ComponentFixture<AccountTransactionsTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountTransactionsTableDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountTransactionsTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
