import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { Skill } from 'src/app/data/skill';
import { SkillService } from '../../skill/service/skill.service';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { Project } from 'src/app/data/project';
import { ProjectSkill } from 'src/app/data/project-skill';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';
import { StatTypes } from './stat-types';
import { CinematicService } from '../cinematic.service';

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
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		TestBed.configureTestingModule({});

		const projectService: ProjectService = TestBed.inject(ProjectService);
		expect(projectService).toBeDefined();
		projectService.allProjects = createProjects();

		const skillService: SkillService = TestBed.inject(SkillService);
		expect(skillService).toBeDefined();
		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(5, 'Java'));
		skillService.allSkills.push(new Skill(9, 'Typescript'));
	});

	it('dashboardService.processSkillDistribution testing with volumes.', () => {
		const service: DashboardService = TestBed.inject(DashboardService);
		expect(service).toBeDefined();

		const tiles = service.processSkillDistribution(true, 1, StatTypes.FilesSize);
		console.log (tiles);
	});

});
