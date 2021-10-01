import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { EvaluationDistribution } from 'src/app/data/EvalutionDistribution';
import { Project } from '../../data/project';
import { CinematicService } from '../cinematic.service';
import { ReferentialService } from '../referential/referential.service';
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

	it('should retrieve the appropriate distribution on the staff coverage solely.', () => {
		project.staffEvaluation = 5;
		expect(projectService.appropriateDistribution(project)).toBeDefined();
		expect(projectService.appropriateDistribution(project)).toEqual(new EvaluationDistribution(100, undefined, undefined));
	});

	it('should retrieve the appropriate distribution on the audit evaluation solely.', () => {
		project.auditEvaluation = 8;
		expect(projectService.appropriateDistribution(project)).toBeDefined();
		expect(projectService.appropriateDistribution(project)).toEqual(new EvaluationDistribution(undefined, undefined, 100));
	});

	it('should retrieve the appropriate distribution on the Sonar quotation solely.', () => {
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 2, 'totalNumberLinesOfCode': 1000 }});
		expect(projectService.appropriateDistribution(project)).toEqual(new EvaluationDistribution(undefined, 100, undefined));
	});

	it('should retrieve the appropriate distribution with the 3 evaluations.', () => {
		project.staffEvaluation = 5;
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 2, 'totalNumberLinesOfCode': 1000 }});
		project.auditEvaluation = 8;
		expect(projectService.appropriateDistribution(project)).toBeDefined();
		expect(projectService.appropriateDistribution(project)).toEqual(new EvaluationDistribution(20, 20, 60));
	});

	it('should NULL if no risk evaluation is available for an empty project.', () => {
		project.staffEvaluation = -1;
		project.sonarProjects = null;
		project.auditEvaluation = 0;
		expect(projectService.appropriateDistribution(project)).toBeUndefined();
	});

});
