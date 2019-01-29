import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogProjectGhostsComponent } from './dialog-project-ghosts.component';

describe('DialogProjectGhostsComponent', () => {
  let component: DialogProjectGhostsComponent;
  let fixture: ComponentFixture<DialogProjectGhostsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogProjectGhostsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogProjectGhostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
