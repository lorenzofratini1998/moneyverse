import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewTransactionComponent } from './overview-transaction.component';

describe('OverviewTransactionComponent', () => {
  let component: OverviewTransactionComponent;
  let fixture: ComponentFixture<OverviewTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverviewTransactionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverviewTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
