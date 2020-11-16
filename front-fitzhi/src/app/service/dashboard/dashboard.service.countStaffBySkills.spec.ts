import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { SkillService } from '../skill.service';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { StaffListService } from 'src/app/staff-list-service/staff-list.service';
import { Skill } from 'src/app/data/skill';
import { CinematicService } from '../cinematic.service';
describe('DashboardService', () => {

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		TestBed.configureTestingModule({});

		/**
		 * Create an array of mock of staff.
		 */
		function createAllStaffForDev(): Collaborator[] {
			const staff: Collaborator[] = [];
			let i: number;
			for (i = 0; i < 4; i++) {
				staff.push(createStaffForDev(i));
			}
			return staff;
		}

		function createStaffForDev(ind: number): Collaborator {
			const staff = new Collaborator();
			staff.idStaff = ind;
			staff.firstName = 'firstname ' + ind;
			staff.lastName = 'lastname ' + ind;
			// The staff member with id = 1 is inactive
			staff.active = (ind !== 1);
			// The staff member with id = 3 is external
			staff.external = (ind === 3);

			staff.experiences = [];

			staff.experiences.push(new Experience(1, 'skill 1', 1));
			staff.experiences.push(new Experience(2, 'skill zwei', 2));
			if (ind < 3) {
				staff.experiences.push(new Experience(4, 'skill four', 4));
			}

			return staff;
		}

		const staffListService: StaffListService = TestBed.inject(StaffListService);
		staffListService.allStaff = createAllStaffForDev();

		const skillService: SkillService = TestBed.inject(SkillService);
		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'skill 1'));
		skillService.allSkills.push(new Skill(2, 'skill zwei'));
		skillService.allSkills.push(new Skill(4, 'skill four'));

	});

	it('"dashboardService.countStaffBySkills()" for ALL staff members & ALL levels (for development purpose)',
		() => {

		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();

		const aggregateData = service.countStaffBySkills(true, 1);

		expect(3).toEqual(aggregateData[1]);
		expect(3).toEqual(aggregateData[2]);
		// Not equal to 3 because we add an experience only 'if (ind < 3)'
		expect(2).toEqual(aggregateData[4]);

	});

	it('"dashboardService.countStaffBySkills()" for INTERN staff members & ALL levels (for development purpose)',
		() => {

		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();

		const aggregateData = service.countStaffBySkills(false, 1);
		expect(2).toEqual(aggregateData[1]);
		expect(2).toEqual(aggregateData[2]);
		expect(2).toEqual(aggregateData[4]);

	});


});
