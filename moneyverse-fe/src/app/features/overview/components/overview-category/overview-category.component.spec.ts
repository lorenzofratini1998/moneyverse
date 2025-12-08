import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewCategoryComponent } from './overview-category.component';

describe('OverviewCategoryComponent', () => {
  let component: OverviewCategoryComponent;
  let fixture: ComponentFixture<OverviewCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverviewCategoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverviewCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
