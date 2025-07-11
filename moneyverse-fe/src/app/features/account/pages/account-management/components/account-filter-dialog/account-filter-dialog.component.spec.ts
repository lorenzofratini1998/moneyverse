import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountFilterDialogComponent} from './account-filter-dialog.component';

describe('AccountFilterComponent', () => {
  let component: AccountFilterDialogComponent;
  let fixture: ComponentFixture<AccountFilterDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountFilterDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountFilterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
