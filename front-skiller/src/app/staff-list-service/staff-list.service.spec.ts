import { TestBed, inject } from '@angular/core/testing';

import { StaffListService } from './staff-list.service';

describe('ListStaffService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StaffListService]
    });
  });

  it('should be created', inject([StaffListService], (service: StaffListService) => {
    expect(service).toBeTruthy();
  }));
});
