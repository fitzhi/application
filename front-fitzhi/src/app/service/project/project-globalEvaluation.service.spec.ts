import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from '../../data/project';
import { CinematicService } from '../cinematic.service';
import { ReferentialService } from '../referential.service';
import { ProjectService } from './project.service';


describe('ProjectService', () => {
	let projectService: ProjectService;
	let project: Project;

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, ReferentialService, CinematicService],
			imports: [HttpClientTestingModule, HttpClientModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		projectService = TestBed.inject(ProjectService);
		project = new Project(1789, 'The revolutionary project');
	});

	it('should calculate an evaluation based on a perfect staff.', () => {
		project.staffEvaluation = 0;
		expect(projectService.globalEvaluation(project)).toBe(10);
	});

	it('should calculate an evaluation based on staff coverage solely.', () => {
		project.staffEvaluation = 5;
		expect(projectService.globalEvaluation(project)).toBe(5);
	});

	it('should calculate an evaluation based on the audit solely.', () => {
		project.auditEvaluation = 80;
		expect(projectService.globalEvaluation(project)).toBe(8);
	});

	it('should calculate an evaluation based on Sonar solely.', () => {
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 20, 'totalNumberLinesOfCode': 1000 }});
		expect(projectService.globalEvaluation(project)).toBe(2);
	});

	it('should calculate an evaluation based on the 3 evaluations (staff, sonar, audit).', () => {
		project.staffEvaluation = 5;
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 20, 'totalNumberLinesOfCode': 1000 }});
		project.auditEvaluation = 80;
		expect(projectService.globalEvaluation(project)).toBe(6);
	});

	it('should calculate an evaluation based on the 2 evaluations (staff, sonar).', () => {
		project.staffEvaluation = 1;
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 40, 'totalNumberLinesOfCode': 1000 }});
		expect(projectService.globalEvaluation(project)).toBe(7);
	});

	it('should calculate an evaluation based on the 2 evaluations (staff, audit).', () => {
		project.staffEvaluation = 3;
		project.auditEvaluation = 80;
		expect(projectService.globalEvaluation(project)).toBe(7);
	});

	it('should return "undefined" for an empty project.', () => {
		project.staffEvaluation = -1;
		project.auditEvaluation = 0;
		expect(projectService.globalEvaluation(project)).toBeUndefined();
	});

});
