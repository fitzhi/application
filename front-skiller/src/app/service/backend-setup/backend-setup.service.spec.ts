import { TestBed } from '@angular/core/testing';

import { BackendSetupService } from './backend-setup.service';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('BackendSetupService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

	it('should be created', () => {
		const service: BackendSetupService = TestBed.get(BackendSetupService);
		expect(service).toBeTruthy();
	});
});
