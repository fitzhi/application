import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InitTest } from '../test/init-test';


describe('ProjectService', () => {

	function createProject(id: number, name: string): Project {
		const project = new Project();
		project.name = name;
		project.id = id;
		project.sonarProjects = [];

		return project;
	}


	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	it('should be created', () => {
		const service: ProjectService = TestBed.get(ProjectService);
		expect(service).toBeTruthy();
	});

	it('testing the method projectService.updateProjectsCollections', () => {
		const service: ProjectService = TestBed.get(ProjectService);
		service.allProjects = [];
		service.allProjects.push(createProject(1, 'one'));
		service.allProjects.push(createProject(2, 'two'));
		expect(2).toEqual(service.allProjects.length);

		service.updateProjectsCollection(createProject(3, 'four'));
		expect(3).toEqual(service.allProjects.length);

		service.updateProjectsCollection(createProject(3, 'three'));
		expect(3).toEqual(service.allProjects.length);
		expect('three').toEqual(service.allProjects[2].name);

	});

	it('testing the method projectService.parserUrl(\'/project/(number)\')', () => {
		const service: ProjectService = TestBed.get(ProjectService);
		expect(undefined).toEqual(service.parseUrl('/project'));
		expect(33).toEqual(service.parseUrl('/project/33'));
	});

	it('testing the method projectService.parserUrl(\'/project/33/staff\')', () => {
		const service: ProjectService = TestBed.get(ProjectService);
		expect(33).toEqual(service.parseUrl('/project/33/staff'));
	});

	it('testing the method projectService.parserUrl(\'/project/1/staff\')', () => {
		const service: ProjectService = TestBed.get(ProjectService);
		expect(1).toEqual(service.parseUrl('/project/1/staff'));
	});
});
