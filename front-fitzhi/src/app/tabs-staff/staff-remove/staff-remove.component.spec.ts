import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffRemoveComponent } from './staff-remove.component';

describe('StaffRemoveComponent', () => {
  let component: StaffRemoveComponent;
  let fixture: ComponentFixture<StaffRemoveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffRemoveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffRemoveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
