import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';

import { TreemapProjectsService } from './treemap-projects.service';

describe('TreemapProjectsService', () => {
	let service: TreemapProjectsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [TreemapProjectsService, DashboardService, ReferentialService, CinematicService]
		});
		service = TestBed.inject(TreemapProjectsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
