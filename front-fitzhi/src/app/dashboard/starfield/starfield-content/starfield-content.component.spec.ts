import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StarfieldContentComponent } from './starfield-content.component';

describe('StarfieldContentComponent', () => {
  let component: StarfieldContentComponent;
  let fixture: ComponentFixture<StarfieldContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StarfieldContentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StarfieldContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
