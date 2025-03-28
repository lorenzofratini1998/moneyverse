import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PreferenceFormComponent} from './preference-form.component';

describe('PreferenceFormComponent', () => {
  let component: PreferenceFormComponent;
  let fixture: ComponentFixture<PreferenceFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreferenceFormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PreferenceFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
