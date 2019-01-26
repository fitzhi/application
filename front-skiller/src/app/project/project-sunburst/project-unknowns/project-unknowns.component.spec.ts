import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectUnknownsComponent } from './project-unknowns.component';

describe('ProjectUnknownsComponent', () => {
  let component: ProjectUnknownsComponent;
  let fixture: ComponentFixture<ProjectUnknownsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectUnknownsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectUnknownsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
