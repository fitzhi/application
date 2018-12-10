import { TestBed, inject } from '@angular/core/testing';

import { ListSkillService } from './list-skill.service';

describe('ListSkillService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ListSkillService]
    });
  });

  it('should be created', inject([ListSkillService], (service: ListSkillService) => {
    expect(service).toBeTruthy();
  }));
});
