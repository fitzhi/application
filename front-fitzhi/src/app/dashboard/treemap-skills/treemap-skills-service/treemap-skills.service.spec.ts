import { TestBed } from '@angular/core/testing';

import { TreemapSkillsService } from './treemap-skills.service';

describe('TreemapService', () => {
	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: TreemapSkillsService = TestBed.inject(TreemapSkillsService);
		expect(service).toBeTruthy();
	});
});
