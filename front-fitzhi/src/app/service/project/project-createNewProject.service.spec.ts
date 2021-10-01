import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../../data/project';
import { ProjectService } from './project.service';
import { HttpTestingController, HttpClientTestingModule, TestRequest } from '@angular/common/http/testing';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { ReferentialService } from '../referential/referential.service';
import { SkillService } from '../../skill/service/skill.service';
import { FileService } from '../file.service';
import { MessageService } from '../../interaction/message/message.service';
import { SunburstCinematicService } from '../../tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { CinematicService } from '../cinematic.service';
import { ListProjectsService } from 'src/app/tabs-project/list-project/list-projects-service/list-projects.service';
import { doesNotReject } from 'assert';
import { of } from 'rxjs';
import { isEmpty } from 'rxjs/operators';


describe('ProjectService.createNewProject$(...) behavior', () => {
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let projectService: ProjectService;

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

		projectService = TestBed.inject(ProjectService);

		projectService.allProjects = [];
		httpTestingController = TestBed.inject(HttpTestingController);

	});

	it('should be created sucessfully.', () => {
		expect(projectService).toBeTruthy();
	});


	it('should loadProject$ with the returned location.', done => {

		projectService.project = new Project (-1, 'The sixth project');

		const spyLoadProject = spyOn(projectService, 'loadProject$')
			.and.returnValue(of(new Project(6, 'The sixth project')));

		projectService.createNewProject$().subscribe({
			next: project => {
				expect(project.id).toEqual(6);
				expect(project.name).toEqual('The sixth project');
				done();
			}
		});

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqApi1.request.method).toEqual('POST');
		reqApi1.flush(
			{},
			{
				headers: {
					'location': 'LOCATION'
				},
				status: 201,
				statusText: 'redirect 201'
			}
		);

		expect(spyLoadProject).toHaveBeenCalled();
	});

	it('should return en EMPTY observable, if the backend does not return a location.', done => {

		projectService.project = new Project (-1, 'The sixth project');

		const spyLoadProject = spyOn(projectService, 'loadProject$');

		projectService.createNewProject$().pipe(isEmpty()).subscribe({
			next: b => {
				expect(b).toBeTrue();
				done();
			}
		});

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqApi1.request.method).toEqual('POST');
		reqApi1.flush(
			{},
			{
				headers: {
				},
				status: 201,
				statusText: 'Empty location'
			}
		);

		expect(spyLoadProject).not.toHaveBeenCalled();
	});

	it('should return en EMPTY observable, if the backend returns an error.', done => {

		projectService.project = new Project (-1, 'The sixth project');

		const spyLoadProject = spyOn(projectService, 'loadProject$');

		projectService.createNewProject$().pipe(isEmpty()).subscribe({
			next: b => {
				expect(b).toBeTrue();
				done();
			}
		});

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqApi1.request.method).toEqual('POST');
		reqApi1.flush(
			{},
			{
				headers: {
				},
				status: 400,
				statusText: 'Bad request'
			}
		);

		expect(spyLoadProject).not.toHaveBeenCalled();
	});

});
