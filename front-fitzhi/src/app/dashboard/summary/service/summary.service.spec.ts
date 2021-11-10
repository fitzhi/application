import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';

import { SummaryService } from './summary.service';

describe('SummaryService', () => {
	let service: SummaryService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ HttpClientTestingModule, MatDialogModule ],
			providers: [ DashboardService, ReferentialService, CinematicService]

		});
		service = TestBed.inject(SummaryService);
	});

	it('should be created without error.', () => {
		expect(service).toBeTruthy();
	});
});
