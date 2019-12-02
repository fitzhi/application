import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditBaseComponentComponent } from './audit-base-component.component';

describe('AuditBaseComponentComponent', () => {
  let component: AuditBaseComponentComponent;
  let fixture: ComponentFixture<AuditBaseComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuditBaseComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuditBaseComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
