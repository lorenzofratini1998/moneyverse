import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DarkModeButtonComponent} from './dark-mode-button.component';

describe('DarkModeToggleComponent', () => {
  let component: DarkModeButtonComponent;
  let fixture: ComponentFixture<DarkModeButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DarkModeButtonComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DarkModeButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
