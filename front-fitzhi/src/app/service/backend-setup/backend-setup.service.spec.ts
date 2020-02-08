import { TestBed } from '@angular/core/testing';

import { BackendSetupService } from './backend-setup.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('BackendSetupService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		providers: [BackendSetupService],
		imports: [HttpClientTestingModule],
	}));

	it('should be created', () => {
		const service: BackendSetupService = TestBed.get(BackendSetupService);
		expect(service).toBeTruthy();
	});
});
