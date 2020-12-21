import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { HttpTestingController, HttpClientTestingModule, TestRequest } from '@angular/common/http/testing';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { ReferentialService } from './referential.service';
import { SkillService } from '../skill/service/skill.service';
import { FileService } from './file.service';
import { MessageService } from '../interaction/message/message.service';
import { SunburstCinematicService } from '../project/project-sunburst/service/sunburst-cinematic.service';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { Skill } from '../data/skill';
import { CinematicService } from './cinematic.service';


describe('ProjectService', () => {
	let httpMock: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let projectService: ProjectService;
	let req1: TestRequest;
	let req2: TestRequest;
	let req: TestRequest;

	function createProject(id: number, name: string): Project {
		const project = new Project();
		project.name = name;
		project.id = id;
		project.sonarProjects = [];

		return project;
	}

	const mockProject2 = {
		id: 2,
		name: 'Two',
		skills: {'1': {'idSkill': 1, 'numberOfFiles': 222, 'totalFilesSize': 222222 }},
		mapSkills: {}
	};

	const mockProject3 = {
		id: 3,
		name: 'Three',
		skills: {'1': {'idSkill': 1, 'numberOfFiles': 333, 'totalFilesSize': 333333 }},
		mapSkills: {}
	};

	const mockSkills = [
		{
			id: 1,
			fullname: 'Java'
		}
	];

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
		backendSetupService.saveUrl('http://localhost:8080');

		const skillService = TestBed.inject(SkillService);
		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'java'));

		projectService = TestBed.inject(ProjectService);
		projectService.allProjects = [];
		projectService.allProjects.push(createProject(1, 'one'));
		projectService.allProjects.push(createProject(2, 'two'));
		expect(2).toEqual(projectService.allProjects.length);
		httpMock = TestBed.inject(HttpTestingController);

	});

	it('testing the method projectService.actualizeProject', () => {

		expect(projectService.allProjects[1].mapSkills.get(1)).toBeUndefined();
		//
		// We UPDATE a project in the array.
		//
		projectService.actualizeProject(2);
		expect(2).toEqual(projectService.allProjects.length);
		expect('two').toEqual(projectService.allProjects[1].name);
		expect(2).toEqual(projectService.allProjects[1].id);


		req1 = httpMock.expectOne('http://localhost:8080/api/project/id/2');
		expect(req1.request.method).toBe('GET');
		req1.flush(mockProject2);

		expect(projectService.allProjects[1].mapSkills.get(1)).toBeDefined();
		expect(222).toEqual(projectService.allProjects[1].mapSkills.get(1).numberOfFiles);
		expect(222222).toEqual(projectService.allProjects[1].mapSkills.get(1).totalFilesSize);

		//
		// We ADD a project in the array.
		//
		projectService.actualizeProject(3);

		req2 = httpMock.expectOne('http://localhost:8080/api/project/id/3');
		expect(req2.request.method).toBe('GET');
		req2.flush(mockProject3);

		expect(3).toEqual(projectService.allProjects.length);
		expect(3).toEqual(projectService.allProjects[2].id);
		expect('Three').toEqual(projectService.allProjects[2].name);

		expect(projectService.allProjects[2].mapSkills.get(1)).toBeDefined();
		expect(333).toEqual(projectService.allProjects[2].mapSkills.get(1).numberOfFiles);
		expect(333333).toEqual(projectService.allProjects[2].mapSkills.get(1).totalFilesSize);

		req = httpMock.expectOne('http://localhost:8080/api/skill');
		expect(req.request.method).toBe('GET');
		req.flush(mockSkills);

		httpMock.verify();
	});

});
