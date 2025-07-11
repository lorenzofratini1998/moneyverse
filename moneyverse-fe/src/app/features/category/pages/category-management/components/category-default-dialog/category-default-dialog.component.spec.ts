import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryDefaultDialogComponent} from './category-default-dialog.component';

describe('DefaultCategoryModalComponent', () => {
  let component: CategoryDefaultDialogComponent;
  let fixture: ComponentFixture<CategoryDefaultDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryDefaultDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryDefaultDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
