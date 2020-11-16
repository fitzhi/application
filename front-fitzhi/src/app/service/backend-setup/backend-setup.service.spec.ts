import { TestBed, async } from '@angular/core/testing';

import { BackendSetupService } from './backend-setup.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('BackendSetupService', () => {
	let service: BackendSetupService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			providers: [BackendSetupService],
			imports: [HttpClientTestingModule]
			})
		.compileComponents();
		service = TestBed.inject(BackendSetupService);
	}));

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
