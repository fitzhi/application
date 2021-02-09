import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';

import { SsewatcherService } from './ssewatcher.service';

describe('SsewatcherService', () => {
  let service: SsewatcherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReferentialService, BackendSetupService, ProjectService, CinematicService, MessageBoxService],
      imports: [HttpClientTestingModule, MatDialogModule]
    });
    service = TestBed.inject(SsewatcherService);
  });

  it('should be created successfully.', () => {
    expect(service).toBeTruthy();
  });
});
