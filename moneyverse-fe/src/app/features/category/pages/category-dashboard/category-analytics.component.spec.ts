import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryAnalyticsComponent} from './category-analytics.component';

describe('CategoryDashboardComponent', () => {
  let component: CategoryAnalyticsComponent;
  let fixture: ComponentFixture<CategoryAnalyticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryAnalyticsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryAnalyticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
