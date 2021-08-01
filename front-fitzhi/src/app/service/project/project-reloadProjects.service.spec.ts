import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { not } from '@angular/compiler/src/output/output_ast';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { NOT_MODIFIED, UNAUTHORIZED } from 'http-status-codes';
import { Constants } from 'src/app/constants';
import { MessageService } from '../../interaction/message/message.service';
import { SkillService } from '../../skill/service/skill.service';
import { SunburstCinematicService } from '../../tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { CinematicService } from '../cinematic.service';
import { FileService } from '../file.service';
import { ReferentialService } from '../referential.service';
import { ProjectService } from './project.service';
import { ProjectsListenerService } from './projects-listener.service';

describe('ProjectService.reloadProjects()', () => {
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let projectService: ProjectService;
	let projectsListenerService: ProjectsListenerService;

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
		projectsListenerService = TestBed.inject(ProjectsListenerService);

		httpTestingController = TestBed.inject(HttpTestingController);

	});

	it('should invoke takeInAccountProjects() if the projects collection is correctly loaded.', done => {

		const spyTakeInAccountProjects = spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.flush(
			{
				id: 0,
				name: 'The test project'
			},
			{
				headers: { 'ETag': 'a_testing_Etag' }
			}
		);

		expect(spyTakeInAccountProjects).toHaveBeenCalled();
		done();
	});

	it('should keep the transferred ETag in the session storage after the load of projects.', done => {
		const spyTakeInAccountProjects = spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.flush(
			{
				id: 0,
				name: 'The test project'
			},
			{
				headers: { 'ETag': 'a_testing_Etag' }
			}
		);

		expect(sessionStorage.getItem(Constants.ETAG_PROJECTS)).toBe('a_testing_Etag');
		done();
	});

	it('should avoid to invoke takeInAccountProjects() if the projects loading failed.', done => {

		const spyTakeInAccountProjects = spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.error(new ErrorEvent('error'), { status: UNAUTHORIZED, statusText: 'Unauthorized resource' });

		expect(spyTakeInAccountProjects).not.toHaveBeenCalled();
		done();
	});

	it('should interrupt the Projects listener loop if the load fails with an unexpected error.', done => {

		spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);
		const spyProjectsListenerService = spyOn(projectsListenerService, 'interruptProjectsListener').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.error(new ErrorEvent('error'), { status: UNAUTHORIZED, statusText: 'Unauthorized resource' });

		expect(spyProjectsListenerService).toHaveBeenCalled();
		done();
	});

	it('should NOT interrupt the Projects listener loop if the server returned an NOT_MODIFIED status error (ETag feature).', done => {

		spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);
		const spyProjectsListenerService = spyOn(projectsListenerService, 'interruptProjectsListener').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.error(new ErrorEvent('error'), { status: NOT_MODIFIED, statusText: 'Projects unchanged' });

		expect(spyProjectsListenerService).not.toHaveBeenCalled();
		done();
	});

});
