import { HttpClientModule, HttpHeaders } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { DeclaredSonarServer } from '../data/declared-sonar-server';
import { InitTest } from '../test/init-test';
import { SonarService } from './sonar.service';


describe('SonarService', () => {

	let httpTestingController: HttpTestingController;
	let sonarService: SonarService;

	const URL_SONAR = 'https://sonar.server:9000';



	beforeEach(async() => {

		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: [HttpClientTestingModule, HttpClientModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

	});

	beforeEach(() => {
		sonarService = TestBed.inject(SonarService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});


	it('should load correctly the Sonar version, and create a "SonarServer" object with a USER/PASS inside the application.', done => {

		const dss = new DeclaredSonarServer(URL_SONAR, 'admin', 'password');
		sonarService.initSonarServer$(dss).subscribe({
			next: sonarServer => {
				expect(sonarServer.sonarVersion).toBe('3.14116');
				expect(sonarServer.sonarOn).toBeTrue();
				expect(sonarServer.user).toBe('admin');
				expect(sonarServer.password).toBe('password');
				expect(sonarServer.login).toBeUndefined();
				done();
			}
		});

		const req = httpTestingController.expectOne(URL_SONAR + '/api/server/version');
		expect(req.request.method).toBe('GET');
		req.flush('3.14116');

	});

	it('should load correctly the Sonar version, and create a "SonarServer" object with a valid LOGIN inside the application.', done => {

		const dss = new DeclaredSonarServer(URL_SONAR, undefined , undefined, 'my-token');
		sonarService.initSonarServer$(dss).subscribe({
			next: sonarServer => {
				expect(sonarServer.sonarVersion).toBe('3.14116');
				expect(sonarServer.sonarOn).toBeTrue();
				expect(sonarServer.user).toBeUndefined();
				expect(sonarServer.password).toBeUndefined();
				expect(sonarServer.login).toBe('my-token');
				done();
			}
		});

		const req = httpTestingController.expectOne(URL_SONAR + '/api/server/version');
		expect(req.request.method).toBe('GET');
		req.flush('3.14116');

	});

	it('should consider the server offline if the Sonar does not return its version.', done => {

		const dss = new DeclaredSonarServer(URL_SONAR, undefined , undefined, 'my-token');
		sonarService.initSonarServer$(dss).subscribe({
			next: sonarServer => {
				expect(sonarServer.sonarOn).toBeFalse();
				expect(sonarServer.sonarVersion).toBeUndefined();
				expect(sonarServer.user).toBeUndefined();
				expect(sonarServer.password).toBeUndefined();
				expect(sonarServer.login).toBeUndefined();
				done();
			}
		});

		const req = httpTestingController.expectOne(URL_SONAR + '/api/server/version');
		expect(req.request.method).toBe('GET');
		const error = new ErrorEvent('error');
		const httpHeaders = new HttpHeaders();
		req.error(
			error,
			{
				headers: httpHeaders,
				status: 404,
				statusText: 'Not found!',
			}
		);
	});

});
