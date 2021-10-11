import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../../data/project';
import { ProjectService } from './project.service';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { InitTest } from '../../test/init-test';


describe('ProjectService', () => {
	let service: ProjectService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		service = TestBed.inject(ProjectService);

	});

	it('should reject a project without any evaluation.', () => {
		const spy = spyOn(service, 'calculateSonarEvaluation').and.returnValue(0);
		const project = new Project(1, 'Test');
		project.staffEvaluation = -1;
		project.auditEvaluation = 0;
		expect(service.hasBeenEvaluated(project)).toBe(false);
		expect(spy).toHaveBeenCalled();
	});

	it('should accept a project with a STAFF evaluation.', () => {
		const spy = spyOn(service, 'calculateSonarEvaluation').and.returnValue(0);
		const project = new Project(1, 'Test');
		project.staffEvaluation = 1;
		project.auditEvaluation = -1;
		expect(service.hasBeenEvaluated(project)).toBe(true);
		expect(spy).toHaveBeenCalled();
	});

	it('should accept a project with a AUDIT evaluation.', () => {
		const spy = spyOn(service, 'calculateSonarEvaluation').and.returnValue(0);
		const project = new Project(1, 'Test');
		project.staffEvaluation = 0;
		project.auditEvaluation = 1;
		expect(service.hasBeenEvaluated(project)).toBe(true);
		expect(spy).toHaveBeenCalled();
	});

	it('should accept a project with a SONAR evaluation.', () => {
		const spy = spyOn(service, 'calculateSonarEvaluation').and.returnValue(1);
		const project = new Project(1, 'Test');
		project.staffEvaluation = 0;
		project.auditEvaluation = 0;
		expect(service.hasBeenEvaluated(project)).toBe(true);
		expect(spy).toHaveBeenCalled();
	});

});
