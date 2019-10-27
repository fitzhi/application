import { TestBed } from '@angular/core/testing';
import { RootTestModule } from '../root-test/root-test.module';
import { SonarService } from './sonar.service';
import { Project } from '../data/project';
import { SonarProject } from '../data/SonarProject';
import { ProjectSonarMetricValue } from '../data/project-sonar-metric-value';


describe('SonarService', () => {

	function createProjectWithMetric(metric: string, weight: number, value: number): Project {
		const project = new Project();
		project.id = 1;
		project.sonarProjects = [];

		const sonarProject = new SonarProject();
		project.sonarProjects.push(sonarProject);
		sonarProject.key = 'keySonar';
		project.sonarProjects.push();
		sonarProject.projectSonarMetricValues = [];
		const p1 = new ProjectSonarMetricValue(metric, weight, value);
		sonarProject.projectSonarMetricValues.push(p1);

		return project;
	}


	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

	it('should be created', () => {
		const service: SonarService = TestBed.get(SonarService);
		expect(service).toBeTruthy();
	});

	it('testing the method evaluateSonarProject with Bugs', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('bugs', 40, 1);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('testing the method evaluateSonarProject when no Bug', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('bugs', 40, 0);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(40).toEqual(result);
	});

	it('testing the method evaluateSonarProject with ZERO code_smells', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('code_smells', 40, 0);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(40).toEqual(result);
	});

	it('testing the method evaluateSonarProject with 10 code_smells', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('code_smells', 40, 10);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(32).toEqual(result);
	});

	it('testing the method evaluateSonarProject with 1000 code_smells', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('code_smells', 40, 1000);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});


	it('testing the method evaluateSonarProject with a Test coverage of 14%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('coverage', 10, 0.14);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect('1.4').toEqual(result.toFixed(1));
	});

	it('testing the method evaluateSonarProject with a density of duplicated lines of 2%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('duplicated_lines_density', 10, 2);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect('9.8').toEqual(result.toFixed(1));
	});

	it('testing the method evaluateSonarProject with a technical debt of 30 minutes', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 30);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(10).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 4 hours', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 240);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(9).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 4000 minutes', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 4000);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(5).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 5 days', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 7200);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(1).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 100000 hours (Shame in you)', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 100000);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a maintanibiliy of 80%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_index', 10, 0.8);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a maintainability of 80%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('security_rating', 10, 0.8);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a reliability of 80%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('reliability_rating', 20, 0.5);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(10).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with an alert status set to ON', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('alert_status', 20, 0);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with an alert status set to OFF', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('alert_status', 30, 1);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(30).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with multiple metrics', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('bugs', 40, 0);
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('coverage', 30, 0.14));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_rating', 30, 5000));

		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(47.2).toEqual(result);
	});

});
