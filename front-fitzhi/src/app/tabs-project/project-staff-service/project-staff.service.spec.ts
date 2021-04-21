import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, inject } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { FileService } from 'src/app/service/file.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';

import { ProjectStaffService } from './project-staff.service';

describe('ProjectStaffService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ProjectStaffService, StaffListService, StaffListService, FileService],
			imports: [MatDialogModule, HttpClientTestingModule]
		});
	});

	it('should be created', inject([ProjectStaffService], (service: ProjectStaffService) => {
		expect(service).toBeTruthy();
	}));
});
