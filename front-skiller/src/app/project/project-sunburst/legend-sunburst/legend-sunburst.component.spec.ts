import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogLegendSunburstComponent } from './legend-sunburst.component';

describe('DialogLegendSunburstComponent', () => {
  let component: DialogLegendSunburstComponent;
  let fixture: ComponentFixture<DialogLegendSunburstComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DialogLegendSunburstComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogLegendSunburstComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
