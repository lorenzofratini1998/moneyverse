import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountCategorySelectComponent} from './account-category-select.component';

describe('AccountCategorySelectComponent', () => {
  let component: AccountCategorySelectComponent;
  let fixture: ComponentFixture<AccountCategorySelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountCategorySelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountCategorySelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
