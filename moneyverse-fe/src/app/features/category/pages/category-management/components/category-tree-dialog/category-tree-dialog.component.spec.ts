import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CategoryTreeDialogComponent} from './category-tree-dialog.component';

describe('CategoryTreeComponent', () => {
  let component: CategoryTreeDialogComponent;
  let fixture: ComponentFixture<CategoryTreeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryTreeDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CategoryTreeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
