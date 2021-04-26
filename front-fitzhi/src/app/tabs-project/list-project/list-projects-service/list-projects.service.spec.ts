import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';
import { ReferentialService } from '../../../service/referential.service';
import { CinematicService } from '../../../service/cinematic.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';

describe('ListProjectsService', () => {
	let service: ListProjectsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ProjectService, MessageService, ReferentialService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule]
		});
		service = TestBed.inject(ListProjectsService);
	});

	it('should be created without error', () => {
		expect(service).toBeTruthy();
	});


});
