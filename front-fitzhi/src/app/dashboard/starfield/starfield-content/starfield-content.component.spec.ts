import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { StarfieldService } from '../service/starfield.service';
import { StarfieldContentComponent } from './starfield-content.component';


describe('StarfieldContentComponent', () => {
  let component: StarfieldContentComponent;
  let fixture: ComponentFixture<StarfieldContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StarfieldContentComponent ],
			providers: [StarfieldService, StaffService, FileService, MessageBoxService, SkillService],
			imports: [MatDialogModule, HttpClientTestingModule]
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
