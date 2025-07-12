import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TagTableComponent} from './tag-table.component';

describe('TagTableComponent', () => {
  let component: TagTableComponent;
  let fixture: ComponentFixture<TagTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagTableComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TagTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
