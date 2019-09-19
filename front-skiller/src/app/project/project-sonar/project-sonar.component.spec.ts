import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectSonarComponent } from './project-sonar.component';

describe('ProjectSonarComponent', () => {
  let component: ProjectSonarComponent;
  let fixture: ComponentFixture<ProjectSonarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectSonarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectSonarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
