import { TestBed, inject } from '@angular/core/testing';

import { ListSkillService } from './list-skill.service';
import { RootTestModule } from '../root-test/root-test.module';

describe('ListSkillService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ListSkillService],
			imports: [RootTestModule]
		});
	});

	it('should be created', inject([ListSkillService], (service: ListSkillService) => {
		expect(service).toBeTruthy();
	}));
});
