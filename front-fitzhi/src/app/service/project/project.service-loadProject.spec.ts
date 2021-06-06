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


describe('ProjectService.loadProject$(...) behavior', () => {
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let projectService: ProjectService;
	let skillService: SkillService;
	let messageService: MessageService;

	function createProject(id: number, name: string): Project {
		const project = new Project();
		project.name = name;
		project.id = id;
		project.sonarProjects = [];

		return project;
	}

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService,
				ReferentialService, SkillService, FileService, MessageService, SunburstCinematicService, BackendSetupService, CinematicService],
			imports: [HttpClientTestingModule, HttpClientModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		skillService = TestBed.inject(SkillService);

		projectService = TestBed.inject(ProjectService);
		messageService = TestBed.inject(MessageService);

		projectService.allProjects = [];
		projectService.allProjects.push(createProject(1, 'one'));
		projectService.allProjects.push(createProject(2, 'two'));
		projectService.allProjects.push(createProject(3, 'three'));
		projectService.allProjects.push(createProject(4, 'four'));
		projectService.allProjects.push(createProject(5, 'five'));
		expect(5).toEqual(projectService.allProjects.length);

		httpTestingController = TestBed.inject(HttpTestingController);

	});

	it('should be created sucessfully.', () => {
		expect(projectService).toBeTruthy();
	});


	it('should update the complete set of projects "allProjects" with the newly created project.', done => {

		const spyLoadProject = spyOn(projectService, 'addProject').and.callThrough();

		projectService.loadProject$('URL_OF_SERVER_LOCATION_PROJECT_CREATED').subscribe({
			next: project => {
				expect(project.id).toEqual(6);
				expect(project.name).toEqual('The sixth project');

				const prj = projectService.allProjects.find(p => p.id === 6);
				expect(prj).not.toBeNull();
				done();
			}
		});

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER_LOCATION_PROJECT_CREATED');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush(
			{
				id: 6,
				name: 'The sixth project'
			}
		);

		expect(spyLoadProject).toHaveBeenCalled();
	});

	it('should fill the current "projectService.project" object with the newly created project.', done => {

		const spyLoadProject = spyOn(projectService, 'addProject').and.callThrough();

		projectService.loadProject$('URL_OF_SERVER_LOCATION_PROJECT_CREATED').subscribe({
			next: project => {
				expect(projectService.project.id).toEqual(6);
				expect(projectService.project.name).toEqual('The sixth project');
				done();
			}
		});

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER_LOCATION_PROJECT_CREATED');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush(
			{
				id: 6,
				name: 'The sixth project'
			}
		);
	});

});
