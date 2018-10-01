import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffProjectsComponent } from './staff-projects.component';

describe('StaffProjectsComponent', () => {
  let component: StaffProjectsComponent;
  let fixture: ComponentFixture<StaffProjectsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffProjectsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
