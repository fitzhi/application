import { TestBed } from '@angular/core/testing';

import { TreemapService } from './treemap.service';

describe('TreemapService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TreemapService = TestBed.get(TreemapService);
    expect(service).toBeTruthy();
  });
});
