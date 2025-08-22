import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DashboardFilterDrawerComponent} from './dashboard-filter-drawer.component';

describe('DashboardFilterDrawerComponent', () => {
  let component: DashboardFilterDrawerComponent;
  let fixture: ComponentFixture<DashboardFilterDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardFilterDrawerComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DashboardFilterDrawerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
