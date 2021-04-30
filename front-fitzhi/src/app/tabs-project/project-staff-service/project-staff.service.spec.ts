import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, inject } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Collaborator } from 'src/app/data/collaborator';
import { Contributor } from 'src/app/data/contributor';
import { FileService } from 'src/app/service/file.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { ListProjectsService } from '../list-project/list-projects-service/list-projects.service';

import { ProjectStaffService } from './project-staff.service';

describe('ProjectStaffService', () => {
	let projectStaffService: ProjectStaffService;
	let staffListService: StaffListService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ProjectStaffService, StaffListService, StaffListService, FileService, ReferentialService],
			imports: [MatDialogModule, HttpClientTestingModule]
		});
		projectStaffService = TestBed.inject(ProjectStaffService);
		staffListService = TestBed.inject(StaffListService);
	});

	it('findContributor should return an existing contributor', () => {
		
		const c1 = new Contributor();
		c1.idStaff = 1515;
		c1.fullname = 'Francois the 1st';
		projectStaffService.contributors.push(c1);

		const c2 = new Contributor();
		c2.idStaff = 1796;
		c2.fullname = 'Napoleon Bonaparte';
		projectStaffService.contributors.push(c2);

		const c = projectStaffService.findContributor(1515);
		expect(c).toBeDefined();
		expect(c.fullname).toBe('Francois the 1st');

	});

	it('findContributor should create a NEW contributor for an existing STAFF member', () => {
		
		const c1 = new Contributor();
		c1.idStaff = 1515;
		c1.fullname = 'Francois the 1st';
		projectStaffService.contributors.push(c1);

		const c2 = new Contributor();
		c2.idStaff = 1796;
		c2.fullname = 'Napoleon Bonaparte';
		projectStaffService.contributors.push(c2);

		const staff = new Collaborator();
		staff.idStaff = 1571;
		staff.firstName = 'Juan';
		staff.lastName = 'of Austria';
		staff.external = true;
		staff.active = true;
		staffListService.allStaff.push(staff)

		const c = projectStaffService.findContributor(1571);
		expect(c).toBeDefined();
		expect(c.fullname).toBe('Juan of Austria');
		expect(c.active).toBeTruthy();
		expect(c.external).toBeTruthy();

		const found = projectStaffService.contributors.find(cont => cont.idStaff = 1571);
		expect(found).toBeDefined();
	});

	it('findContributor should create a NEW contributor AS WELL for an UNKNOWN STAFF member', () => {
		
		const c1 = new Contributor();
		c1.idStaff = 1515;
		c1.fullname = 'Francois the 1st';
		projectStaffService.contributors.push(c1);

		const c2 = new Contributor();
		c2.idStaff = 1796;
		c2.fullname = 'Napoleon Bonaparte';
		projectStaffService.contributors.push(c2);

		const c = projectStaffService.findContributor(1571);
		expect(c).toBeDefined();
		expect(c.fullname).toBe('Unknown 1571');
		expect(c.active).toBeFalsy();
		expect(c.external).toBeFalsy();

		const found = projectStaffService.contributors.find(cont => cont.idStaff = 1571);
		expect(found).toBeDefined();
	});

});
