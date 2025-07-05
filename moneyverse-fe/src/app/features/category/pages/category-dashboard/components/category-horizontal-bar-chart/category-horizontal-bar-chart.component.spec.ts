import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryHorizontalBarChartComponent} from './category-horizontal-bar-chart.component';

describe('CategoryHorizontalBarChartComponent', () => {
  let component: CategoryHorizontalBarChartComponent;
  let fixture: ComponentFixture<CategoryHorizontalBarChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryHorizontalBarChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryHorizontalBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
