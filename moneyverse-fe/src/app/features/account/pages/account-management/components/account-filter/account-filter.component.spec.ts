import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountFilterComponent} from './account-filter.component';

describe('AccountFilterComponent', () => {
  let component: AccountFilterComponent;
  let fixture: ComponentFixture<AccountFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountFilterComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
