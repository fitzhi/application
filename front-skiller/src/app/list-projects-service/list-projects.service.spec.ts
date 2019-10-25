import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';
import { RootTestModule } from '../root-test/root-test.module';

describe('ListProjectsService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ListProjectsService],
			imports: [RootTestModule]
		});
	});

	it('should be created', inject([ListProjectsService], (service: ListProjectsService) => {
		expect(service).toBeTruthy();
	}));
});
