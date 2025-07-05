import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryDashboardComponent} from './category-dashboard.component';

describe('CategoryDashboardComponent', () => {
  let component: CategoryDashboardComponent;
  let fixture: ComponentFixture<CategoryDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryDashboardComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
