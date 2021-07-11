import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../../data/project';
import { ProjectService } from './project.service';
import { HttpTestingController, HttpClientTestingModule, TestRequest } from '@angular/common/http/testing';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { ReferentialService } from '../referential.service';
import { SkillService } from '../../skill/service/skill.service';
import { FileService } from '../file.service';
import { MessageService } from '../../interaction/message/message.service';
import { SunburstCinematicService } from '../../tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { CinematicService } from '../cinematic.service';
import { ListProjectsService } from 'src/app/tabs-project/list-project/list-projects-service/list-projects.service';
import { doesNotReject } from 'assert';
import { SonarProject } from 'src/app/data/SonarProject';


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

	it('should calculate an evaluation based on staff coverage solely.', () => {
		project.staffEvaluation = 5;
		expect(projectService.globalEvaluation(project)).toBe(5);
	});

	it('should calculate an evaluation based on the audit solely.', () => {
		project.auditEvaluation = 8;
		expect(projectService.globalEvaluation(project)).toBe(8);
	});

	it('should calculate an evaluation based on Sonar solely.', () => {
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 2, 'totalNumberLinesOfCode': 1000 }});
		expect(projectService.globalEvaluation(project)).toBe(2);
	});

	it('should calculate an evaluation based on the 3 evaluations (staff, sonar, audit).', () => {
		project.staffEvaluation = 5;
		project.sonarProjects = [];
		project.sonarProjects.push({key: 'key', name: 'name', sonarEvaluation: { 'evaluation': 2, 'totalNumberLinesOfCode': 1000 }});
		project.auditEvaluation = 8;
		expect(projectService.globalEvaluation(project)).toBe(6);
	});

});
