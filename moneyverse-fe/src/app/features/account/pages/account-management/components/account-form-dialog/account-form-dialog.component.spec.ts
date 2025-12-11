import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountFormDialogComponent} from './account-form-dialog.component';

describe('AccountFormComponent', () => {
  let component: AccountFormDialogComponent;
  let fixture: ComponentFixture<AccountFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountFormDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
