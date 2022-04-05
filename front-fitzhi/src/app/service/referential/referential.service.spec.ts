import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReferentialService } from './referential.service';
import { BackendSetupService } from '../backend-setup/backend-setup.service';


describe('ReferentialService', () => {

	let service: ReferentialService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ReferentialService, BackendSetupService],
			imports: [HttpClientTestingModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		service = TestBed.inject(ReferentialService);
		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('HOST_URL');
	});

	it('should be correctly created.', () => {
		expect(service).toBeTruthy();
	});

	it('should load correctly all referentials.', () => {

		const httpMock = TestBed.inject(HttpTestingController);

		service.loadAllReferentials();

		const req0 = httpMock.expectOne('HOST_URL/api/referential/openid-server');
		expect(req0.request.method).toBe('GET');
		req0.flush([]);

		const req1 = httpMock.expectOne('HOST_URL/api/referential/profiles');
		expect(req1.request.method).toBe('GET');
		req1.flush([]);

		const req2 = httpMock.expectOne('HOST_URL/api/referential/riskLegends');
		expect(req2.request.method).toBe('GET');
		req2.flush([]);

		const req3 = httpMock.expectOne('HOST_URL/api/referential/treemap-skills-coverage');
		expect(req3.request.method).toBe('GET');
		req3.flush([]);

		const req4 = httpMock.expectOne('HOST_URL/api/referential/sonar-servers');
		expect(req4.request.method).toBe('GET');
		req4.flush([]);

		const req5 = httpMock.expectOne('HOST_URL/api/referential/ecosystem');
		expect(req5.request.method).toBe('GET');
		req5.flush([]);

		const req6 = httpMock.expectOne('HOST_URL/api/referential/supported-metrics');
		expect(req6.request.method).toBe('GET');
		req6.flush([]);

		const req7 = httpMock.expectOne('HOST_URL/api/referential/audit-topics');
		expect(req7.request.method).toBe('GET');
		req7.flush([]);

		httpMock.verify();
	});


});
