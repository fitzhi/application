import { TestBed } from '@angular/core/testing';

import { PieDashboardService } from './pie-dashboard.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';

describe('PieDashboardService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [ HttpClientTestingModule, MatDialogModule ],
		providers: [ReferentialService]
	}));

	it('should be created', () => {
		const service: PieDashboardService = TestBed.get(PieDashboardService);
		expect(service).toBeTruthy();
	});
});
