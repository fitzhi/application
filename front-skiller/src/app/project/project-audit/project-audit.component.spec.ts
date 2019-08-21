import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditComponent } from './project-audit.component';

describe('ProjectAuditComponent', () => {
  let component: ProjectAuditComponent;
  let fixture: ComponentFixture<ProjectAuditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectAuditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectAuditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
