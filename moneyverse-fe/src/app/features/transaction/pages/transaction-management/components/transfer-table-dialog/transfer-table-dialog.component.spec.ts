import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TransferTableDialogComponent} from './transfer-table-dialog.component';

describe('TransferTableComponent', () => {
  let component: TransferTableDialogComponent;
  let fixture: ComponentFixture<TransferTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferTableDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TransferTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
