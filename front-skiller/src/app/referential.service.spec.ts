import { TestBed, inject } from '@angular/core/testing';

import { ReferentialService } from './referential.service';

describe('ReferentialService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReferentialService]
    });
  });

  it('should be created', inject([ReferentialService], (service: ReferentialService) => {
    expect(service).toBeTruthy();
  }));
});
