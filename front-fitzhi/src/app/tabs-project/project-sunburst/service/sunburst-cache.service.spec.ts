import { TestBed } from '@angular/core/testing';

import { SunburstCacheService } from './sunburst-cache.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('SunburstCacheService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [HttpClientTestingModule, HttpClientModule, MatDialogModule],
		providers: [ReferentialService, CinematicService]
	}));

	it('should be created', () => {
		const service: SunburstCacheService = TestBed.inject(SunburstCacheService);
		expect(service).toBeTruthy();
	});
});
