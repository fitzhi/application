import { TestBed, inject } from '@angular/core/testing';

import { CollaboraterService } from './collaborater.service';

describe('CollaboraterService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CollaboraterService]
    });
  });

  it('should be created', inject([CollaboraterService], (service: CollaboraterService) => {
    expect(service).toBeTruthy();
  }));
});
