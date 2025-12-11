import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BudgetFormDialogComponent} from './budget-form-dialog.component';

describe('BudgetFormDialogComponent', () => {
  let component: BudgetFormDialogComponent;
  let fixture: ComponentFixture<BudgetFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetFormDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(BudgetFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
