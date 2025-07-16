import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CustomChipComponent} from './custom-chip.component';

describe('CustomChipComponent', () => {
  let component: CustomChipComponent;
  let fixture: ComponentFixture<CustomChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomChipComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CustomChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
