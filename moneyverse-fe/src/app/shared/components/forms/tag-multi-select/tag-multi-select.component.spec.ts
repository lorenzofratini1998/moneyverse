import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TagMultiSelectComponent} from './tag-multi-select.component';

describe('TagMultiSelectComponent', () => {
  let component: TagMultiSelectComponent;
  let fixture: ComponentFixture<TagMultiSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagMultiSelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TagMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
