import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListContributorsComponent } from './list-contributors.component';

describe('ListContributorsComponent', () => {
  let component: ListContributorsComponent;
  let fixture: ComponentFixture<ListContributorsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListContributorsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListContributorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
