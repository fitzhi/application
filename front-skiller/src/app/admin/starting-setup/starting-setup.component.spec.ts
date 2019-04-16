import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StartingSetupComponent } from './starting-setup.component';

describe('StartingSetupComponent', () => {
  let component: StartingSetupComponent;
  let fixture: ComponentFixture<StartingSetupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StartingSetupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StartingSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
