import { TestBed, inject } from '@angular/core/testing';

import { CinematicService } from './cinematic.service';

describe('CinematicService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CinematicService]
    });
  });

  it('should be created', inject([CinematicService], (service: CinematicService) => {
    expect(service).toBeTruthy();
  }));
});
