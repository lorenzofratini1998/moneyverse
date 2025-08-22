import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionFilterFormComponent} from './transaction-filter-form.component';

describe('TransactionFilterFormComponent', () => {
  let component: TransactionFilterFormComponent;
  let fixture: ComponentFixture<TransactionFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionFilterFormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
