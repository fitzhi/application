import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapProjectsChartComponent } from './treemap-projects-chart.component';

describe('TeamProjectsChartComponent', () => {
  let component: TreemapProjectsChartComponent;
  let fixture: ComponentFixture<TreemapProjectsChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TreemapProjectsChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreemapProjectsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
