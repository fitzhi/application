import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditTaskComponent } from './audit-task.component';

describe('AuditTaskComponent', () => {
  let component: AuditTaskComponent;
  let fixture: ComponentFixture<AuditTaskComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuditTaskComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuditTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
