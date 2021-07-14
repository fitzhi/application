import { TestBed } from '@angular/core/testing';

import { TreemapProjectsService } from './treemap-projects.service';

describe('TreemapProjectsService', () => {
  let service: TreemapProjectsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TreemapProjectsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
