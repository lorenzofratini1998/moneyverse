import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AmountSliderComponent} from './amount-slider.component';

describe('AmountSliderComponent', () => {
  let component: AmountSliderComponent;
  let fixture: ComponentFixture<AmountSliderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AmountSliderComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AmountSliderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
