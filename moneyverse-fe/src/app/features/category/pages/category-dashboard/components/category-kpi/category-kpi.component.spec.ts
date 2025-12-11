import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryKpiComponent} from './category-kpi.component';

describe('CategoryKpiComponent', () => {
  let component: CategoryKpiComponent;
  let fixture: ComponentFixture<CategoryKpiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryKpiComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryKpiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
