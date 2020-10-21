import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { InitTest } from '../test/init-test';


describe('ProjectService', () => {
	let httpMock: HttpTestingController;

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
		skills: {'1': {'idSkill': 1, 'numberOfFiles': 302, 'totalFilesSize': 981519 }}
	};

	const mockProject3 = {
		id: 3,
		name: 'Three',
	};

	const mockSkills = [
		{
			id: 1,
			fullname: 'Java'
		}
	];

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		httpMock = TestBed.inject(HttpTestingController);

	});

	it('should be created', () => {
		const service: ProjectService = TestBed.inject(ProjectService);
		expect(service).toBeTruthy();
	});

	it('testing the method projectService.parserUrl(\'/project/(number)\')', () => {
		const service: ProjectService = TestBed.inject(ProjectService);
		expect(undefined).toEqual(service.parseUrl('/project'));
		expect(33).toEqual(service.parseUrl('/project/33'));
	});

	it('testing the method projectService.parserUrl(\'/project/33/staff\')', () => {
		const service: ProjectService = TestBed.inject(ProjectService);
		expect(33).toEqual(service.parseUrl('/project/33/staff'));
	});

	it('testing the method projectService.parserUrl(\'/project/1/staff\')', () => {
		const service: ProjectService = TestBed.inject(ProjectService);
		expect(1).toEqual(service.parseUrl('/project/1/staff'));
	});
});
