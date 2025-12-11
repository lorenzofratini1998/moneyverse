import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryDistributionChartComponent} from './category-distribution-chart.component';

describe('CategoryHorizontalBarChartComponent', () => {
  let component: CategoryDistributionChartComponent;
  let fixture: ComponentFixture<CategoryDistributionChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryDistributionChartComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryDistributionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
