import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { ProjectSkill } from 'src/app/data/project-skill';
import { SkillService } from '../../skill/service/skill.service';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { CinematicService } from '../cinematic.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { DashboardService } from './dashboard.service';

describe('DashboardService.calculateGeneralAverage', () => {

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
	});

	it('calculation for a single and simple project.', () => {
		const p = new Project (1, 'the project');
		p.mapSkills = new Map();
		p.mapSkills.set(1, new ProjectSkill(1, 100, 1000));
		p.mapSkills.set(2, new ProjectSkill(2, 200, 2000));
		p.active = true;
		projectService.allProjects.push(p);

		const spy = spyOn(projectService, 'globalEvaluation').and.returnValues(5);
		const result = service.calculateGeneralAverage();
		expect(result).toBe(5);
		expect(spy).toHaveBeenCalled();
	});

	it('calculation for 2 projects.', () => {
		let p = new Project (1, 'the One');
		p.mapSkills = new Map();
		p.mapSkills.set(1, new ProjectSkill(1, 100, 1000));
		p.active = true;
		projectService.allProjects.push(p);

		p = new Project (2, 'the Two');
		p.mapSkills = new Map();
		p.mapSkills.set(2, new ProjectSkill(2, 100, 1000));
		p.active = true;
		projectService.allProjects.push(p);

		const spy = spyOn(projectService, 'globalEvaluation').and.returnValues(7);
		const result = service.calculateGeneralAverage();
		expect(result).toBe(3);
		expect(spy).toHaveBeenCalledTimes(2);
	});

});
