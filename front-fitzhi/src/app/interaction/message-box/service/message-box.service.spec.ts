import { TestBed } from '@angular/core/testing';

import { MessageBoxService } from './message-box.service';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('MessageBoxService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		providers: [MessageBoxService],
		imports: [MatDialogModule, BrowserAnimationsModule]
	}));

	it('should be created', () => {
		const service: MessageBoxService = TestBed.inject(MessageBoxService);
		expect(service).toBeTruthy();
	});
});
