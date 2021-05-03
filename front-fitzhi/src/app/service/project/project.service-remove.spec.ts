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


describe('ProjectService', () => {
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

	it('should remove correctly a project from its local collection.', () => {
		projectService.removeLocalProject(4);
		expect(projectService.allProjects.length).toEqual(4);
		expect(projectService.allProjects.findIndex(p => (p.id === 4))).toEqual(-1);
	});

	it('should throw an error if the service cannot remove an unknwown id from its local collection.', () => {
		try {
			projectService.removeLocalProject(1789);
		} catch (e) {
			expect(e.message).toBe( 'WTF : Should not pass here !');
		}
	});

	it('should remove correctly a project after a successful call to the Rest API.', done => {
		
		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/skill');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush([
			{
				id: 1,
				title: 'Java'
			}
		]);
		
		projectService.project = new Project(3, 'The third');
		projectService.removeApiProject$().subscribe({
			next: b => {

				expect(projectService.allProjects.length).toEqual(4);
				expect(projectService.allProjects.findIndex(p => (p.id === 3))).toEqual(-1);
				
				done();
			}

		})
		
		const reqApi2 = httpTestingController.expectOne('URL_OF_SERVER/api/project/3');
		expect(reqApi2.request.method).toEqual('DELETE');
		reqApi2.flush('true');

		
	});

	it('should correctly handle a 400 Bad Request Error when calling the Rest API.', done => {
		
		const spyMessageService = spyOn(messageService, 'error').and.returnValue();

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/skill');
		reqApi1.flush([
			{
				id: 1,
				title: 'Java'
			}
		]);


		projectService.project = new Project(3, 'The third');
		projectService.removeApiProject$().subscribe({
			next: b => {
			
				expect(b).toBeFalse();
				expect(projectService.allProjects.length).toEqual(5);
				expect(spyMessageService).toHaveBeenCalled();

				done();
			}
			
		});
		
		const reqApi2 = httpTestingController.expectOne('URL_OF_SERVER/api/project/3');
		expect(reqApi2.request.method).toEqual('DELETE');
		reqApi2.error(new ErrorEvent('400'), {});

	});

});
