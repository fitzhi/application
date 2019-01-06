import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectSunburstComponent } from './project-sunburst.component';

describe('ProjectSunburstComponent', () => {
  let component: ProjectSunburstComponent;
  let fixture: ComponentFixture<ProjectSunburstComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectSunburstComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectSunburstComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
