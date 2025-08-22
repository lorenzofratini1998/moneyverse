import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountMultiSelectComponent} from './account-multi-select.component';

describe('AccountMultiSelectComponent', () => {
  let component: AccountMultiSelectComponent;
  let fixture: ComponentFixture<AccountMultiSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountMultiSelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
