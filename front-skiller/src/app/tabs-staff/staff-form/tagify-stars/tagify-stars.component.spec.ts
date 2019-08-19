import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TagifyStarsComponent } from './tagify-stars.component';

describe('TagifyStarsComponent', () => {
  let component: TagifyStarsComponent;
  let fixture: ComponentFixture<TagifyStarsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TagifyStarsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TagifyStarsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
