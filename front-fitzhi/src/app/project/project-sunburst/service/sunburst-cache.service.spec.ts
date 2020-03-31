import { TestBed } from '@angular/core/testing';

import { SunburstCacheService } from './sunburst-cache.service';

describe('SunburstCacheService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SunburstCacheService = TestBed.get(SunburstCacheService);
    expect(service).toBeTruthy();
  });
});
