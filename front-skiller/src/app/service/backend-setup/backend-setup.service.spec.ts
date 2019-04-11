import { TestBed } from '@angular/core/testing';

import { BackendSetupService } from './backend-setup.service';

describe('BackendSetupService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BackendSetupService = TestBed.get(BackendSetupService);
    expect(service).toBeTruthy();
  });
});
