import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogUpdatedProjectGhostsComponent } from './dialog-updated-project-ghosts.component';

describe('DialogUpdatedProjectGhostsComponent', () => {
  let component: DialogUpdatedProjectGhostsComponent;
  let fixture: ComponentFixture<DialogUpdatedProjectGhostsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogUpdatedProjectGhostsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogUpdatedProjectGhostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
