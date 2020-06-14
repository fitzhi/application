import { TestBed, inject } from '@angular/core/testing';

import { ListSkillService } from './list-skill.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ListSkillService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ListSkillService],
			imports: [HttpClientTestingModule]
		});
	});

	it('should be created', inject([ListSkillService], (service: ListSkillService) => {
		expect(service).toBeTruthy();
	}));
});
