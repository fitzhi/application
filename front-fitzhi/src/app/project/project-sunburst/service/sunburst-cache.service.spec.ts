import { TestBed } from '@angular/core/testing';

import { SunburstCacheService } from './sunburst-cache.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';

describe('SunburstCacheService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		imports: [HttpClientTestingModule, HttpClientModule, MatDialogModule],
		providers: [ReferentialService]
	}));

	it('should be created', () => {
		const service: SunburstCacheService = TestBed.get(SunburstCacheService);
		expect(service).toBeTruthy();
	});
});
