import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionKpiComponent} from './transaction-kpi.component';

describe('TransactionKpiComponent', () => {
  let component: TransactionKpiComponent;
  let fixture: ComponentFixture<TransactionKpiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionKpiComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionKpiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
