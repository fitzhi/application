import { TestBed, async } from '@angular/core/testing';

import { BackendSetupService } from './backend-setup.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FirstConnection } from 'src/app/data/first-connection';
import { HttpHeaders } from '@angular/common/http';

describe('BackendSetupService', () => {
	let service: BackendSetupService;
	let httpMock: HttpTestingController;
	
	beforeEach(async(() => {
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
		
		service.isVeryFirstConnection('TEST_URL').subscribe(firstConnection => {
			expect(firstConnection.connected).toBeTruthy();
			expect(firstConnection.validUrl).toBeNull();
		});

		const req = httpMock.expectOne('TEST_URL/api/admin/isVeryFirstConnection');
		expect(req.request.method).toBe("GET");
		req.flush(('true'));
	});

	it ('should handle a "302 FOUND" response', () => {
		
		service.isVeryFirstConnection('TEST_URL').subscribe(firstConnection => {
			expect(firstConnection.connected).toBeFalsy();
			expect(firstConnection.validUrl).toBe(null);
		});

		const response = httpMock.expectOne('TEST_URL/api/admin/isVeryFirstConnection');
		expect(response.request.method).toBe("GET");
		response.error(<any>{}, { 
			status: 302, 
			statusText: 'Found',  
			headers: new HttpHeaders({ 'Content-Type': 'text/html', 'Location':  'HTTS_TEST_URL'}) });
	});



	afterEach(() => {
		httpMock.verify();
	  });
});
