import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackendSetupComponent } from './backend-setup.component';

describe('BackendSetupComponent', () => {
  let component: BackendSetupComponent;
  let fixture: ComponentFixture<BackendSetupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BackendSetupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BackendSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
