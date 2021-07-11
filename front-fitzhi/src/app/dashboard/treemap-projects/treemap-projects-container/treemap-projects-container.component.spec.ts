import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapProjectsContainerComponent } from './treemap-projects-container.component';

describe('TreemapProjectsContainerComponent', () => {
  let component: TreemapProjectsContainerComponent;
  let fixture: ComponentFixture<TreemapProjectsContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TreemapProjectsContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreemapProjectsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
