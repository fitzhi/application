import { HttpHeaders } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { BackendSetupService } from './backend-setup.service';


describe('BackendSetupService', () => {
	let service: BackendSetupService;
	let httpMock: HttpTestingController;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			providers: [BackendSetupService],
			imports: [HttpClientTestingModule]
			})
		.compileComponents();
		service = TestBed.inject(BackendSetupService);
		httpMock = TestBed.inject(HttpTestingController);
	}));

	it('should be created without error', () => {
		expect(service).toBeTruthy();
	});

	it ('should handle a successfull connection', () => {

		service.isVeryFirstConnection$('URL_OF_SERVER').subscribe(firstConnection => {
			expect(firstConnection.connected).toBeTruthy();
			expect(firstConnection.validUrl).toBeNull();
		});

		const req = httpMock.expectOne('URL_OF_SERVER/api/admin/isVeryFirstConnection');
		expect(req.request.method).toBe('GET');
		req.flush(('true'));
	});

	it ('should handle a "302 FOUND" response', () => {

		service.isVeryFirstConnection$('URL_OF_SERVER').subscribe(firstConnection => {
			expect(firstConnection.connected).toBeFalsy();
			expect(firstConnection.validUrl).toBe(null);
		});

		const response = httpMock.expectOne('URL_OF_SERVER/api/admin/isVeryFirstConnection');
		expect(response.request.method).toBe('GET');
		response.error(<any>{}, {
			status: 302,
			statusText: 'Found',
			headers: new HttpHeaders({ 'Content-Type': 'text/html', 'Location':  'HTTS_TEST_URL'}) });
	});

	afterEach(() => {
		httpMock.verify();
	});
});
