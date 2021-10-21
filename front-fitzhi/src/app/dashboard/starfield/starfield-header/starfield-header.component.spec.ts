import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StarfieldHeaderComponent } from './starfield-header.component';

describe('StarfieldHeaderComponent', () => {
  let component: StarfieldHeaderComponent;
  let fixture: ComponentFixture<StarfieldHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StarfieldHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StarfieldHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
