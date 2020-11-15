import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SkylineIconComponent } from './skyline-icon.component';


describe('SkylineIconComponent', () => {
  let component: SkylineIconComponent;
  let fixture: ComponentFixture<SkylineIconComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SkylineIconComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SkylineIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the Skyline icon', () => {
    expect(component).toBeTruthy();
  });
});
