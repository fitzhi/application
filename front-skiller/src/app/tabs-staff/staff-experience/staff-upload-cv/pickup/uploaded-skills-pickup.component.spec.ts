import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadedSkillsPickupComponent } from './uploaded-skills-pickup.component';

describe('UploadedSkillsPickupComponent', () => {
  let component: UploadedSkillsPickupComponent;
  let fixture: ComponentFixture<UploadedSkillsPickupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadedSkillsPickupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadedSkillsPickupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
