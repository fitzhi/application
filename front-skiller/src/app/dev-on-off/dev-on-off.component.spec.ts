import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DevOnOffComponent } from './dev-on-off.component';

describe('DevOnOffComponent', () => {
  let component: DevOnOffComponent;
  let fixture: ComponentFixture<DevOnOffComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DevOnOffComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DevOnOffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
