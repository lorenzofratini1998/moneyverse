import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DashboardFilterPanelComponent} from './dashboard-filter-panel.component';

describe('DashboardFilterPanelComponent', () => {
  let component: DashboardFilterPanelComponent;
  let fixture: ComponentFixture<DashboardFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardFilterPanelComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DashboardFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
