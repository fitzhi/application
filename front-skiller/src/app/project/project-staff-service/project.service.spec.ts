import { TestBed, inject } from '@angular/core/testing';

import { ProjectStaffService } from './project-staff.service';

describe('ProjectStaffService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProjectStaffService]
    });
  });

  it('should be created', inject([ProjectStaffService], (service: ProjectStaffService) => {
    expect(service).toBeTruthy();
  }));
});
