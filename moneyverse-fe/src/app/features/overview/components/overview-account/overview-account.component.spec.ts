import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewAccountComponent } from './overview-account.component';

describe('OverviewAccountComponent', () => {
  let component: OverviewAccountComponent;
  let fixture: ComponentFixture<OverviewAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverviewAccountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverviewAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
