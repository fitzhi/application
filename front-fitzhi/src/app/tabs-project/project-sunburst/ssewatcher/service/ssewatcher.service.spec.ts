import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';

import { SsewatcherService } from './ssewatcher.service';

describe('SsewatcherService', () => {
	let service: SsewatcherService;
	let projectService: ProjectService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ReferentialService, BackendSetupService, ProjectService, CinematicService, MessageBoxService],
			imports: [HttpClientTestingModule, MatDialogModule]
		});
		service = TestBed.inject(SsewatcherService);
		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'The Revolutionary project');
	});

	it('should initialize the Listen Server successfully.', done => {
		const spyListenServer = spyOn(service, 'listenServer');
		service.initEventSource('URL');
		expect(spyListenServer).toHaveBeenCalled();
		done();
	});
});
