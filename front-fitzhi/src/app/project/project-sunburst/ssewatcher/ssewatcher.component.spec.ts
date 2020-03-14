import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SSEWatcherComponent } from './ssewatcher.component';

describe('SSEWatcherComponent', () => {
  let component: SSEWatcherComponent;
  let fixture: ComponentFixture<SSEWatcherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SSEWatcherComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SSEWatcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
