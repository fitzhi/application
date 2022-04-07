import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { AuthService } from '../../service/auth/auth.service';
import { ConnectUserFormComponent } from './connect-user-form.component';


describe('ConnectUserFormComponent', () => {
  let component: ConnectUserFormComponent;
  let fixture: ComponentFixture<ConnectUserFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConnectUserFormComponent ],
      providers: [AuthService, BackendSetupService, ProjectService, CinematicService, FileService, MessageBoxService, FormBuilder],
      imports: [HttpClientTestingModule, MatDialogModule, RouterTestingModule, FormsModule, ReactiveFormsModule]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnectUserFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created with error.', () => {
    expect(component).toBeTruthy();
  });
});
