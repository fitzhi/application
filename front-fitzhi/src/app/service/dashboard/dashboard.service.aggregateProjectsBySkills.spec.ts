import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { DashboardService } from './dashboard.service';
import { SkillService } from '../skill.service';
import { Project } from 'src/app/data/project';
import { ProjectSkill } from 'src/app/data/project-skill';
import { ProjectService } from '../project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StaffService } from '../staff.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ReferentialService } from '../referential.service';

describe('DashboardService', () => {

	beforeEach(async () => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ProjectService, SkillService, StaffService, ReferentialService, DashboardService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => TestBed.configureTestingModule({}));

	it('should be created', () => {
		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();
	});

	it('"dashboardService.aggregateProjectsBySkills()" for development purpose', () => {

		/**
		 * Create an array of mock of projects.
		 */
		function createProjectsForDev(): Project[] {
			const projects: Project[] = [];
			let i: number;
			for (i = 0; i < 2; i++) {
				projects.push(createProjectForDev(i));
			}
			return projects;
		}

		function createProjectForDev(ind: number): Project {
			const project = new Project(ind, 'Project ' + ind);
			project.mapSkills.set(1, new ProjectSkill(1, 1, 10));
			project.mapSkills.set(2, new ProjectSkill(2, 2, 20));
			return project;
		}

		const service: DashboardService = TestBed.get(DashboardService);
		expect(service).toBeDefined();

		const projectService: ProjectService = TestBed.get(ProjectService);
		projectService.allProjects = createProjectsForDev();

		const aggregateData = service.aggregateProjectsBySkills();
		console.groupCollapsed('Aggragate data');
		aggregateData.forEach(element => {
			console.log (element.idSkill + ' ' + element.sumNumberOfFiles + ' ' + element.sumTotalFilesSize);
		});
		console.groupEnd();

		let result = aggregateData[0];
		expect(2).toEqual(result.sumNumberOfFiles);
		expect(20).toEqual(result.sumTotalFilesSize);

		result = aggregateData[1];
		expect(4).toEqual(result.sumNumberOfFiles);
		expect(40).toEqual(result.sumTotalFilesSize);

	});


});
