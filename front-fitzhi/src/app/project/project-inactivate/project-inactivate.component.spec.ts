import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectInactivateComponent } from './project-inactivate.component';

describe('ProjectInactivateComponent', () => {
  let component: ProjectInactivateComponent;
  let fixture: ComponentFixture<ProjectInactivateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectInactivateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectInactivateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
