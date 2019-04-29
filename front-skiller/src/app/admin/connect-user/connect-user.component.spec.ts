import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectUserComponent } from './connect-user.component';

describe('ConnectUserComponent', () => {
  let component: ConnectUserComponent;
  let fixture: ComponentFixture<ConnectUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConnectUserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
