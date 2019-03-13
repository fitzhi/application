import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffUploadCvComponent } from './staff-upload-cv.component';

describe('StaffUploadCvComponent', () => {
  let component: StaffUploadCvComponent;
  let fixture: ComponentFixture<StaffUploadCvComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffUploadCvComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffUploadCvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
