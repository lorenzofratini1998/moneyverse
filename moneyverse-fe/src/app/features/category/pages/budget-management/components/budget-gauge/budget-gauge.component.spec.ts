import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BudgetGaugeComponent} from './budget-gauge.component';

describe('BudgetGaugeComponent', () => {
  let component: BudgetGaugeComponent;
  let fixture: ComponentFixture<BudgetGaugeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetGaugeComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(BudgetGaugeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
