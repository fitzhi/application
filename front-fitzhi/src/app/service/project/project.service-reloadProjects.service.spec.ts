import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageService } from '../../interaction/message/message.service';
import { SkillService } from '../../skill/service/skill.service';
import { SunburstCinematicService } from '../../tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { CinematicService } from '../cinematic.service';
import { FileService } from '../file.service';
import { ReferentialService } from '../referential.service';
import { ProjectService } from './project.service';


describe('ProjectService.reloadProjects()', () => {
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let projectService: ProjectService;

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

		httpTestingController = TestBed.inject(HttpTestingController);

	});

	it('should be created sucessfully.', () => {
		expect(projectService).toBeTruthy();
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
			}
		);

		expect(spyTakeInAccountProjects).toHaveBeenCalled();
		done();
	});

	it('should avoid to invoke takeInAccountProjects() if the projects loading failed.', done => {

		const spyTakeInAccountProjects = spyOn(projectService, 'takeInAccountProjects').and.returnValue(null);

		projectService.reloadProjects();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.error(new ErrorEvent('error'), { status: 401, statusText: 'Unauthorized resource' });

		expect(spyTakeInAccountProjects).not.toHaveBeenCalled();
		done();
	});

});
