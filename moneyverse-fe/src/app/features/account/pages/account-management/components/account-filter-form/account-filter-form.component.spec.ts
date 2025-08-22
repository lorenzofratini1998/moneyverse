import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountFilterFormComponent} from './account-filter-form.component';

describe('AccountFilterFormComponent', () => {
  let component: AccountFilterFormComponent;
  let fixture: ComponentFixture<AccountFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountFilterFormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
