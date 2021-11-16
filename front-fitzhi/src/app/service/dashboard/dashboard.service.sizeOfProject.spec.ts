import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { SkillService } from '../../skill/service/skill.service';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { DashboardService } from './dashboard.service';

describe('DashboardService', () => {

	const project = require('./project-fitzhi.json');

	let service: DashboardService;
	let projectService: ProjectService;

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
		projectService.loadMapSkills(project);
	});

	it('calculate without error the size of an empty project.', () => {
		const emptyProject = new Project (1890, 'Empty one');
		expect(service.sizeOfProject(emptyProject)).toBe(0)
	});

	it('calculate the size of the Fitzhi project.', () => {
		console.log (project);
		expect(service.sizeOfProject(project)).toBe(2748096)
	});

});
