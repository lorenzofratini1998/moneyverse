import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DashboardFilterFormComponent} from './dashboard-filter-form.component';

describe('DashboardFilterFormComponent', () => {
  let component: DashboardFilterFormComponent;
  let fixture: ComponentFixture<DashboardFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardFilterFormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DashboardFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
