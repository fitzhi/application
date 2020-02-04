import { TestBed } from '@angular/core/testing';
import { RootTestModule } from '../root-test/root-test.module';
import { Project } from '../data/project';
import { ProjectService } from './project.service';


describe('ProjectService', () => {

	function createProject(id: number, name: string): Project {
		const project = new Project();
		project.name = name;
		project.id = id;
		project.sonarProjects = [];

		return project;
	}


	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

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

});
