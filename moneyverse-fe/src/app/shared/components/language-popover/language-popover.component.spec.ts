import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LanguagePopoverComponent} from './language-popover.component';

describe('LanguagePopoverComponent', () => {
  let component: LanguagePopoverComponent;
  let fixture: ComponentFixture<LanguagePopoverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LanguagePopoverComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LanguagePopoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
