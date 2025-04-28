import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DefaultCategoryModalComponent} from './default-category-modal.component';

describe('DefaultCategoryModalComponent', () => {
  let component: DefaultCategoryModalComponent;
  let fixture: ComponentFixture<DefaultCategoryModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DefaultCategoryModalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DefaultCategoryModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
