import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogProjectGhostsDialog } from './dialog-project-ghosts.component';

describe('DialogProjectGhostsComponent', () => {
  let component: DialogProjectGhostsDialog;
  let fixture: ComponentFixture<DialogProjectGhostsDialog>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogProjectGhostsDialog ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogProjectGhostsDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
