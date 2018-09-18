import { TestBed, inject } from '@angular/core/testing';

import { ListStaffService } from './list-staff.service';

describe('ListStaffService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ListStaffService]
    });
  });

  it('should be created', inject([ListStaffService], (service: ListStaffService) => {
    expect(service).toBeTruthy();
  }));
});
