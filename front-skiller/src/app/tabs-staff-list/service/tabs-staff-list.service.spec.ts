import { TestBed } from '@angular/core/testing';

import { TabsStaffListService } from './tabs-staff-list.service';

describe('TabsStaffService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TabsStaffListService = TestBed.get(TabsStaffListService);
    expect(service).toBeTruthy();
  });
});
