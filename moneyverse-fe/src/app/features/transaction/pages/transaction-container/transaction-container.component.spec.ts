import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionContainerComponent} from './transaction-container.component';

describe('TagContainerComponent', () => {
  let component: TransactionContainerComponent;
  let fixture: ComponentFixture<TransactionContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionContainerComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
