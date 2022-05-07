import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../../data/project';
import { ProjectSonarMetricValue } from '../../data/project-sonar-metric-value';
import { SonarServer } from '../../data/sonar-server';
import { SonarProject } from '../../data/SonarProject';
import { InitTest } from '../../test/init-test';
import { SonarService } from './sonar.service';


describe('SonarService', () => {

	let sonarService: SonarService;
	const URL_SONAR = 'https://sonar.server:9000';

	function createProjectWithMetric(metric: string, weight: number, value: number): Project {
		const project = new Project();
		project.urlSonarServer = URL_SONAR;
		project.id = 1;
		project.sonarProjects = [];

		const sonarProject = new SonarProject();
		sonarProject.key = 'keySonar';
		project.sonarProjects.push(sonarProject);

		sonarProject.projectSonarMetricValues = [];
		const p1 = new ProjectSonarMetricValue(metric, weight, value);
		sonarProject.projectSonarMetricValues.push(p1);

		return project;
	}


	beforeEach(async() => {

		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

	});

	beforeEach(() => {
		sonarService = TestBed.inject(SonarService);
		expect(sonarService).toBeTruthy();
		sonarService.sonarServers.push(new SonarServer('1.0.TEST', URL_SONAR));
	});

	it('tests the method evaluateSonarProject with Bugs', () => {
		const project = createProjectWithMetric('bugs', 40, 1);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('tests the method evaluateSonarProject when no Bug', () => {
		const project = createProjectWithMetric('bugs', 40, 0);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(40).toEqual(result);
	});

	it('tests the method evaluateSonarProject with ZERO code_smells', () => {
		const project = createProjectWithMetric('code_smells', 40, 0);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(40).toEqual(result);
	});

	it('tests the method evaluateSonarProject with 10 code_smells', () => {
		const project = createProjectWithMetric('code_smells', 40, 10);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(32).toEqual(result);
	});

	it('tests the method evaluateSonarProject with 1000 code_smells', () => {
		const project = createProjectWithMetric('code_smells', 40, 1000);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});


	it('tests the method evaluateSonarProject with a Test coverage of 14%', () => {
		const project = createProjectWithMetric('coverage', 10, 0.14);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect('1.4').toEqual(result.toFixed(1));
	});

	it('tests the method evaluateSonarProject with a density of duplicated lines of 2%', () => {
		const project = createProjectWithMetric('duplicated_lines_density', 10, 2);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect('9.8').toEqual(result.toFixed(1));
	});

	it('tests the method evaluateSonarProject with a technical debt of 30 minutes', () => {
		const project = createProjectWithMetric('sqale_index', 10, 30);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(10).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a technical debt of 4 hours', () => {
		const project = createProjectWithMetric('sqale_index', 10, 240);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(9).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a technical debt of 800 minutes', () => {
		const project = createProjectWithMetric('sqale_index', 10, 800);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(5).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a technical debt of 5 days', () => {
		const project = createProjectWithMetric('sqale_index', 10, 2760);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(1).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a technical debt of 100000 hours (Shame on you)', () => {
		const project = createProjectWithMetric('sqale_index', 10, 100000);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a Maintainabiliy of 80% (value=2)', () => {
		const project = createProjectWithMetric('sqale_rating', 10, 2);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a security of 80%', () => {
		const project = createProjectWithMetric('security_rating', 10, 2);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(8).toEqual(result);
	});

	it('tests the method evaluateSonarProject with a reliability of 80% (value=2)', () => {
		const project = createProjectWithMetric('reliability_rating', 20, 2);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(16).toEqual(result);
	});

	it('tests the method evaluateSonarProject with an alert status set to ON', () => {
		const project = createProjectWithMetric('alert_status', 20, 0);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(0).toEqual(result);
	});

	it('tests the method evaluateSonarProject with an alert status set to OFF', () => {
		const project = createProjectWithMetric('alert_status', 30, 1);
		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(30).toEqual(result);
	});

	it('tests the method evaluateSonarProject with multiple metrics', () => {
		const project = createProjectWithMetric('bugs', 40, 0);
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('coverage', 30, 0.14));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_index', 30, 3000));

		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(47.2).toEqual(result);
	});

	it('tests the method evaluateSonarProject with the whole possible metrics', () => {
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

		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(65).toEqual(result);
	});

	it('tests the method evaluateSonarProject with some metrics', () => {
		const project = createProjectWithMetric('bugs', 40, 31);
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_rating', 20, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('reliability_rating', 20, 3));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('security_rating', 10, 1));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_index', 10, 136));

		const result = sonarService.evaluateSonarProject (project, 'keySonar');
		expect(51).toEqual(result);
	});

	it('tests the reproduction of a bug. What\'s going on with the default weigths without metrics retrieved', () => {
		const service: SonarService = TestBed.inject(SonarService);

		const project = createProjectWithMetric('bugs', 40, 0);
		// The 3 next metrics will be ignored.
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('sqale_rating', 20, 0));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('reliability_rating', 20, 0));
		project.sonarProjects[0].projectSonarMetricValues
			.push(new ProjectSonarMetricValue('security_rating', 20, 0));

		const result = service.evaluateSonarProject (project, 'keySonar');
		expect(40).toEqual(result);
	});

	it('tests the pair user/password to check the user authorization.', () => {
		const service: SonarService = TestBed.inject(SonarService);
		const sonarServer = new SonarServer('1.0', 'urlSonar', true, 'frvidal', 'nope');
		const headers = service.sonarHeaders(sonarServer);
		expect(headers.get('Authorization')).not.toBeNull();
		expect(headers.get('Authorization')).toBe('Basic ' + btoa('frvidal:nope'));
	});

	it('tests the scenario without security of the SonarQube server.', () => {
		const service: SonarService = TestBed.inject(SonarService);
		const sonarServer = new SonarServer('1.0', 'urlSonar', true);
		const headers = service.sonarHeaders(sonarServer);
		expect(headers.get('Authorization')).toBeNull();
	});

	it('tests the login token to check the user authorization.', () => {
		const service: SonarService = TestBed.inject(SonarService);
		const sonarServer = new SonarServer('1.0', 'urlSonar', true, null, null, 'my-token');
		const headers = service.sonarHeaders(sonarServer);
		expect(headers.get('Authorization')).not.toBeNull();
		expect(headers.get('Authorization')).toBe('Basic ' + btoa('my-token:'));
	});

	it('isActive() should always return FALSE if the given URL is null.', () => {
		expect(sonarService.isActive(null)).toBe(false);
	});

	it('isActive() should return the status of the Sonar server if the given URL is found.', () => {
		const sonarServer1 = new SonarServer('1.0', 'urlSonar1', true);
		const sonarServer2 = new SonarServer('1.0', 'urlSonar2', false);
		sonarService.sonarServers.push(sonarServer1);
		sonarService.sonarServers.push(sonarServer2);
		expect(sonarService.isActive('urlSonar1')).toBe(true);
		expect(sonarService.isActive('urlSonar2')).toBe(false);
	});

	it('isActive() should ALWAYS return FALSE if the given URL is an unregistered url.', () => {
		const sonarServer = new SonarServer('1.0', 'urlSonar', true);
		sonarService.sonarServers.push(sonarServer)
		expect(sonarService.isActive('unregistered')).toBe(false);
	});

	it('isActive() should ALWAYS return FALSE if 2 (WTF) servers correspond to the given URL.', () => {
		const sonarServer1 = new SonarServer('1.0', 'urlSonar', true);
		const sonarServer2 = new SonarServer('1.0', 'urlSonar', true);
		sonarService.sonarServers.push(sonarServer1)
		sonarService.sonarServers.push(sonarServer2)
		expect(sonarService.isActive('urlSonar')).toBe(false);
	});

});
