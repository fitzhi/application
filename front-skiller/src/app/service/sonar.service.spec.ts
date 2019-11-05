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
		const project = createProjectWithMetric('sqale_index', 10, 30);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(10).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 4 hours', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_index', 10, 240);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(9).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 800 minutes', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_index', 10, 800);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(5).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 5 days', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_index', 10, 2760);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(1).toEqual(result);
	});

	it('testing the method evaluateSonarProject with a technical debt of 100000 hours (Shame on you)', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_index', 10, 100000);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a Maintainabiliy of 80% (value=2)', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('sqale_rating', 10, 2);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a security of 80%', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('security_rating', 10, 2);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with a reliability of 80% (value=2)', () => {
		const service: SonarService = TestBed.get(SonarService);
		const project = createProjectWithMetric('reliability_rating', 20, 2);
		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(16).toEqual(result);
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
			.push(new ProjectSonarMetricValue('sqale_index', 30, 3000));

		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(47.2).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with the whole possible metrics', () => {
		const service: SonarService = TestBed.get(SonarService);

		const project = createProjectWithMetric('bugs', 10, 0);
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('code_smells', 10, 113));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('coverage', 10, 0));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('duplicated_lines_density', 10, 0));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_rating', 20, 1));

		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('alert_status', 10, 0));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('reliability_rating', 10, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('security_rating', 10, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_index', 10, 800));

		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(65).toEqual(result);
	});

	it('Testing the method evaluateSonarProject with some metrics', () => {
		const service: SonarService = TestBed.get(SonarService);

		const project = createProjectWithMetric('bugs', 40, 31);
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_rating', 20, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('reliability_rating', 20, 3));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('security_rating', 10, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_index', 10, 136));

		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(51).toEqual(result);
	});

});
