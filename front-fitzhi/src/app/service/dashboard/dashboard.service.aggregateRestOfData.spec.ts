import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { SkillService } from '../../skill/service/skill.service';
import { ProjectService } from '../project/project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { CinematicService } from '../cinematic.service';
describe('DashboardService', () => {

	function generateSkillProjectsAggregation(): SkillProjectsAggregation[] {
		const aggregations = [];
		for (let i = 0; i < DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM; i++) {
			aggregations.push(new SkillProjectsAggregation(String(i), 100, 100));
		}
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

		const aggregations = generateSkillProjectsAggregation();

	});

	it('"dashboardService.aggregateRestOfData() " for an array whose length is below the limit', () => {

		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();

		const aggregations = generateSkillProjectsAggregation();
		expect(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM).toBe(aggregations.length);

		aggregations.splice(1, 1);

		const resultingAggregations = service.aggregateRestOfData(aggregations);
		expect(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1).toEqual(resultingAggregations.length);
		expect(100).toEqual(resultingAggregations[0].sumNumberOfFiles);

	});

	it('"dashboardService.aggregateRestOfData() " for an array whose length is upper than the limit', () => {
		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();
		const aggregations = generateSkillProjectsAggregation();

		//
		// We add 2 SkillProjectsAggregation
		//
		expect(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM).toBe(aggregations.length);
		aggregations.push(new SkillProjectsAggregation(String(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM), 100, 100));
		aggregations.push(new SkillProjectsAggregation(String(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM + 1), 100, 100));

		const resultingAggregations = service.aggregateRestOfData(aggregations);
		expect(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM).toEqual(resultingAggregations.length);
		expect(100).toEqual(resultingAggregations[0].sumNumberOfFiles);
		expect(300).toEqual(resultingAggregations[DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1].sumNumberOfFiles);
		expect(300).toEqual(resultingAggregations[DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1].sumTotalFilesSize);
	});


});
