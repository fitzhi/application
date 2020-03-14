import { TestBed } from '@angular/core/testing';

import { SseListenerService } from './sse-listener.service';

describe('SseObserverService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: SseListenerService = TestBed.get(SseListenerService);
		expect(service).toBeTruthy();
	});
});
