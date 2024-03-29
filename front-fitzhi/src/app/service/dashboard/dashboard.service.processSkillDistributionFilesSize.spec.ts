import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Skill } from 'src/app/data/skill';
import { SkillService } from '../../skill/service/skill.service';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { DashboardConstants } from './dashboard-constants';
import { DashboardService } from './dashboard.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';


describe('DashboardService', () => {

	function generateSkillProjectsAggregation(): SkillProjectsAggregation[] {
		const aggregations = [];
		for (let i = 0; i < DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1; i++) {
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

		const skillService: SkillService = TestBed.inject(SkillService);
		expect(skillService).toBeDefined();
		skillService.allSkills = [];
		for (let i = 0; i < DashboardConstants.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1; i++) {
			skillService.allSkills.push(new Skill(i, `title for Skill ${i}`));
		}
		skillService.allSkills.push(new Skill(10, 'java'));

	});

	it('"dashboardService.processSkillDistributionFilesSize()" : generates the diagram with tiles', () => {

		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();

		const tiles = service.processSkillDistributionFilesSize(1, generateSkillProjectsAggregation(), {});

		expect(10).toEqual(tiles.length);
		expect(true).toBeDefined(tiles[0].name);
		expect(true).toBeDefined(tiles[0].value);
		expect(43).toEqual(tiles[0].value);
		expect('java').toEqual(tiles[0].name);
	});


});
