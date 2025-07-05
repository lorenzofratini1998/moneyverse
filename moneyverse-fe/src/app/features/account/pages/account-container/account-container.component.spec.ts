import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AccountContainerComponent} from './account-container.component';

describe('AccountContainerComponent', () => {
  let component: AccountContainerComponent;
  let fixture: ComponentFixture<AccountContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountContainerComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AccountContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
