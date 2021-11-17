import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { SkillService } from '../../skill/service/skill.service';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { DashboardService } from './dashboard.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';

describe('DashboardService.globalScoreSkillsDistribution()', () => {

	let service: DashboardService;
	let projectService: ProjectService;

	const skillProjectsAggregation = require('./skill-projects-aggregation.json');
	const skillStaffCount = require('./skill-staff-count.json');

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
		service = TestBed.inject(DashboardService);
		projectService = TestBed.inject(ProjectService);
	});

	it('should return ZERO if the collection of projects is empty.', () => {
		expect(service.globalScoreSkillDistribution()).toBe(0);
	});

	it('should return the previous calculated value (if any).', () => {
		service.skillsCoverageScore = 17;
		expect(service.globalScoreSkillDistribution()).toBe(17);
	});

	it('calculation for a collection of skills.', () => {

		const spySkills = spyOn(service, 'aggregateProjectsBySkills')
			.and.returnValue(skillProjectsAggregation);

		const spyStaff = spyOn(service, 'countStaffBySkills')
			.and.returnValue(skillStaffCount);

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.optimalStaffNumberPerMoOfCode[0] = 8;

		expect(service.globalScoreSkillDistribution()).toBe(67);

	});

	it('calculation for atheFitzhi repository which contains only one perfect skill.', () => {

		const spySkills = spyOn(service, 'aggregateProjectsBySkills')
			.and.returnValue([new SkillProjectsAggregation('1', 1, 1000000)]);
		const spyStaff = spyOn(service, 'countStaffBySkills')
			.and.returnValue({ '1': 4 });

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.optimalStaffNumberPerMoOfCode[0] = 4;

		expect(service.globalScoreSkillDistribution()).toBe(100);

	});


});
