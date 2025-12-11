import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryTrendChartComponent} from './category-trend-chart.component';

describe('CategoryLineChartComponent', () => {
  let component: CategoryTrendChartComponent;
  let fixture: ComponentFixture<CategoryTrendChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryTrendChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryTrendChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
