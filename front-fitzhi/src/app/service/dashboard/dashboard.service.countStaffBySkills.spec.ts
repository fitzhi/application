import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { Skill } from 'src/app/data/skill';
import { SkillService } from '../skill.service';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { Project } from 'src/app/data/project';
import { ProjectFormComponent } from 'src/app/project/project-form/project-form.component';
import { ProjectSkill } from 'src/app/data/project-skill';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { StaffListService } from 'src/app/staff-list-service/staff-list.service';
describe('DashboardService', () => {

	/**
	 * Create an array of mock of skills.
	 */
	function createSkills(): Skill[] {
		const skills: Skill[] = [];
		let i: number;
		for (i = 0; i < 100; i++) {
			skills.push(new Skill(i, 'title for ' + i));
		}
		return skills;
	}

	/**
	 * Create an array of mock of skills.
	 */
	function createStaffs(): Collaborator[] {
		const staffs: Collaborator[] = [];
		let i: number;
		for (i = 0; i < 1000; i++) {
			staffs.push(createStaff(i));
		}
		return staffs;
	}

	function createStaff(ind: number): Collaborator {
		const staff = new Collaborator();
		staff.firstName = 'firstName for ' + ind;
		staff.lastName = 'lastName for ' + ind;
		staff.active = ((ind % 5) === 0);
		staff.external = ((ind % 20) === 0);
		if ((ind % 5) === 0) {
			staff.experiences.push(new Experience(5, 'Title for 5', 0));
		}
		if ((ind % 9) === 0) {
			staff.experiences.push(new Experience(9, 'Title for 9', 0));
		}
		return staff;
	}

	/**
	 * Create an array of mock of projects.
	 */
	function createProjects(): Project[] {
		const projects: Project[] = [];
		let i: number;
		for (i = 0; i < 1000; i++) {
			projects.push(createProject(i));
		}
		return projects;
	}

	function createProject(ind: number): Project {
		const project = new Project(ind, 'Project ' + ind);
		if ((ind % 3) === 0) {
			project.mapSkills.set(5, new ProjectSkill(5, 1, 10));
		}
		if ((ind % 10) === 0) {
			project.mapSkills.set(9, new ProjectSkill(9, 1, 10));
		}
		return project;
	}

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService],
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

		const staffListService: StaffListService = TestBed.get(StaffListService);
		staffListService.allStaff = createAllStaffForDev();
		console.log ('allStaff created', staffListService.allStaff);
	});

	it('"dashboardService.countStaffBySkills()" for ALL staff members & ALL levels (for development purpose)',
		() => {

		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();

		const aggregateData = service.countStaffBySkills(true, 1);
		expect(3).toEqual(aggregateData[1]);
		expect(3).toEqual(aggregateData[2]);
		// Not equal to 3 because we add an experience only 'if (ind < 3)'
		expect(2).toEqual(aggregateData[4]);

	});

	it('"dashboardService.countStaffBySkills()" for INTERN staff members & ALL levels (for development purpose)',
		() => {

		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();

		const aggregateData = service.countStaffBySkills(false, 1);
		expect(2).toEqual(aggregateData[1]);
		expect(2).toEqual(aggregateData[2]);
		expect(2).toEqual(aggregateData[4]);

	});


});
