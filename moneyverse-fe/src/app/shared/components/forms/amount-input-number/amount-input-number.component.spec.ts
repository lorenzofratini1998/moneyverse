import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AmountInputNumberComponent} from './amount-input-number.component';

describe('AmountInputNumberComponent', () => {
  let component: AmountInputNumberComponent;
  let fixture: ComponentFixture<AmountInputNumberComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AmountInputNumberComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AmountInputNumberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
