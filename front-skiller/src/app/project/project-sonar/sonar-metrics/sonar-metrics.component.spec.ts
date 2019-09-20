import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SonarMetricsComponent } from './sonar-metrics.component';

describe('SonarMetricsComponent', () => {
  let component: SonarMetricsComponent;
  let fixture: ComponentFixture<SonarMetricsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SonarMetricsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SonarMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
