import { TestBed } from '@angular/core/testing';

import { SkylineService } from './skyline.service';

describe('SkylineService', () => {
  let service: SkylineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SkylineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
