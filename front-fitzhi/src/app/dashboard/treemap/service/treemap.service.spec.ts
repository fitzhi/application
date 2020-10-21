import { TestBed } from '@angular/core/testing';

import { TreemapService } from './treemap.service';

describe('TreemapService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TreemapService = TestBed.inject(TreemapService);
    expect(service).toBeTruthy();
  });
});
