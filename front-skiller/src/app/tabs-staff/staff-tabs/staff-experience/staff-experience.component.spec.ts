import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffExperienceComponent } from './staff-experience.component';

describe('StaffExperienceComponent', () => {
  let component: StaffExperienceComponent;
  let fixture: ComponentFixture<StaffExperienceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffExperienceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffExperienceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
