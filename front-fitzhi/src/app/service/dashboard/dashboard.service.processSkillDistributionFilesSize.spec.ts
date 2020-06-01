import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { SkillService } from '../skill.service';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { Skill } from 'src/app/data/skill';
import { CinematicService } from '../cinematic.service';

describe('DashboardService', () => {

	function generateSkillProjectsAggregation(): SkillProjectsAggregation[] {
		const aggregations = [];
		for (let i = 0; i < DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1; i++) {
			aggregations.push(new SkillProjectsAggregation(String(i), 0, (i % 3) * 100 + 50));
		}
		aggregations.push(new SkillProjectsAggregation('10', 0, 1000));

		return aggregations;
	}

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		TestBed.configureTestingModule({});

		const skillService: SkillService = TestBed.get(SkillService);
		expect(skillService).toBeDefined();
		skillService.allSkills = [];
		for (let i = 0; i < DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1; i++) {
			skillService.allSkills.push(new Skill(i, 'title for Skill ' + i));
		}
		skillService.allSkills.push(new Skill(10, 'java'));

	});

	it('"dashboardService.processSkillDistributionFilesSize()" : generates the diagram with tiles', () => {

		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();

		const tiles = service.processSkillDistributionFilesSize(generateSkillProjectsAggregation(), {});

		expect(10).toEqual(tiles.length);
		expect(true).toBeDefined(tiles[0].name);
		expect(true).toBeDefined(tiles[0].value);
		expect(43).toEqual(tiles[0].value);
		expect('java').toEqual(tiles[0].name);
	});


});
