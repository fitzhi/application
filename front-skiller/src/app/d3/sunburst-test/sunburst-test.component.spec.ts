import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SunburstTestComponent } from './sunburst-test.component';

describe('SunburstTestComponent', () => {
  let component: SunburstTestComponent;
  let fixture: ComponentFixture<SunburstTestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SunburstTestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SunburstTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
