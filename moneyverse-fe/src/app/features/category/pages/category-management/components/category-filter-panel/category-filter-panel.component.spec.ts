import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryFilterPanelComponent} from './category-filter-panel.component';

describe('CategoryFilterPanelComponent', () => {
  let component: CategoryFilterPanelComponent;
  let fixture: ComponentFixture<CategoryFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryFilterPanelComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
