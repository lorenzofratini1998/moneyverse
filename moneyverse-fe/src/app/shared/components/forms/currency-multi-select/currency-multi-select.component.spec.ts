import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CurrencyMultiSelectComponent} from './currency-multi-select.component';

describe('CurrencyMultiSelectComponent', () => {
  let component: CurrencyMultiSelectComponent;
  let fixture: ComponentFixture<CurrencyMultiSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrencyMultiSelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CurrencyMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
