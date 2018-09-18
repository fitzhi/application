import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';

describe('ListProjectsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ListProjectsService]
    });
  });

  it('should be created', inject([ListProjectsService], (service: ListProjectsService) => {
    expect(service).toBeTruthy();
  }));
});
