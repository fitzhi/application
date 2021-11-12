import { HttpClientTestingModule } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { ListSkillService } from './list-skill.service';


describe('ListSkillService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ListSkillService],
			imports: [HttpClientTestingModule]
		});
	});

	it('should be simply created without error', inject([ListSkillService], (service: ListSkillService) => {
		expect(service).toBeTruthy();
	}));
});
