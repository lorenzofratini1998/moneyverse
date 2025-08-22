import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransactionFilterPanelComponent} from './transaction-filter-panel.component';

describe('TransactionFilterPanelComponent', () => {
  let component: TransactionFilterPanelComponent;
  let fixture: ComponentFixture<TransactionFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionFilterPanelComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransactionFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
