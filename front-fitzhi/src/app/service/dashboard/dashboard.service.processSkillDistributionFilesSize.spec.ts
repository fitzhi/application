import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { SkillService } from '../skill.service';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';

describe('DashboardService', () => {

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		TestBed.configureTestingModule({});
	});

	it('"dashboardService.processSkillDistributionFilesSize()" : generate the diagram with tiles)', () => {

		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();

		const aggregationProjects = [];
		const tiles = service.processSkillDistributionFilesSize(aggregationProjects);
	});


});
