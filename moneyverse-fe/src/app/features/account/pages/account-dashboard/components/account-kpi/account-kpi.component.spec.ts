import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountKpiComponent} from './account-kpi.component';

describe('AccountKpiComponent', () => {
  let component: AccountKpiComponent;
  let fixture: ComponentFixture<AccountKpiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountKpiComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountKpiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
