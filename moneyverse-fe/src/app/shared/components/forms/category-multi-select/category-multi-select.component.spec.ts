import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryMultiSelectComponent} from './category-multi-select.component';

describe('CategoryMultiSelectComponent', () => {
  let component: CategoryMultiSelectComponent;
  let fixture: ComponentFixture<CategoryMultiSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryMultiSelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
