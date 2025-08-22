import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryDefaultGridComponent} from './category-default-grid.component';

describe('CategoryDefaultGridComponent', () => {
  let component: CategoryDefaultGridComponent;
  let fixture: ComponentFixture<CategoryDefaultGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryDefaultGridComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryDefaultGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
