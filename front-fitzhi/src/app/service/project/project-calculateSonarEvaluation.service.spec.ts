import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
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


	it('should replicate correctly the Sonar evaluation for a simple project.', () => {
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 2, 'totalNumberLinesOfCode': 1000 }});
		expect(projectService.calculateSonarEvaluation(project)).toBe(2);
	});

	it('should calculate correctly the mean evaluation for a DOUBLE Sonar projects.', () => {
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key1', name: 'name', sonarEvaluation: { 'evaluation': 3, 'totalNumberLinesOfCode': 100 }});
		project.sonarProjects.push({key: 'key2', name: 'name', sonarEvaluation: { 'evaluation': 5, 'totalNumberLinesOfCode': 2000 }});
		expect(projectService.calculateSonarEvaluation(project)).toBe(5);
	});

});
