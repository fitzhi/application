import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieProjectsComponent } from './pie-projects.component';

describe('PieProjectsComponent', () => {
  let component: PieProjectsComponent;
  let fixture: ComponentFixture<PieProjectsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PieProjectsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PieProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
