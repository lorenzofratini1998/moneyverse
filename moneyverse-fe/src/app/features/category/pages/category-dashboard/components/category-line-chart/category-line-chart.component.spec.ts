import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryLineChartComponent} from './category-line-chart.component';

describe('CategoryLineChartComponent', () => {
  let component: CategoryLineChartComponent;
  let fixture: ComponentFixture<CategoryLineChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryLineChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryLineChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
