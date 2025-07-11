import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountFilterPanelComponent} from './account-filter-panel.component';

describe('AccountFilterPanelComponent', () => {
  let component: AccountFilterPanelComponent;
  let fixture: ComponentFixture<AccountFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountFilterPanelComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
