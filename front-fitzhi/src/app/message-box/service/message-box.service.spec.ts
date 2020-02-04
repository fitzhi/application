import { TestBed } from '@angular/core/testing';

import { MessageBoxService } from './message-box.service';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('MessageBoxService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

	it('should be created', () => {
		const service: MessageBoxService = TestBed.get(MessageBoxService);
		expect(service).toBeTruthy();
	});
});
