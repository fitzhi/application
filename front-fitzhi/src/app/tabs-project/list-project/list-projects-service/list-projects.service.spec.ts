import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';
import { ReferentialService } from '../../../service/referential.service';
import { CinematicService } from '../../../service/cinematic.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { ProjectSkill } from 'src/app/data/project-skill';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Skill } from 'src/app/data/skill';

describe('ListProjectsService', () => {
	let projectService: ProjectService;
	let skillService: SkillService;
	let theService: ListProjectsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ProjectService, MessageService, ReferentialService, CinematicService, SkillService],
			imports: [HttpClientTestingModule, MatDialogModule]
		});
		theService = TestBed.inject(ListProjectsService);
		projectService = TestBed.inject(ProjectService);
		skillService = TestBed.inject(SkillService);

		projectService.allProjects = [];
		projectService.allProjects.push(new Project (1, 'xx'));
		projectService.allProjects.push(new Project (2, 'xDUMMYx'));
		projectService.allProjects.push(new Project (3, 'x dummy x'));
		projectService.allProjects.push(new Project (4, 'x Dummy x'));
		projectService.allProjects.push(new Project (5, 'x dommy x'));

		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'one'));
		skillService.allSkills.push(new Skill(2, 'two'));
		skillService.allSkills.push(new Skill(3, 'three'));
		skillService.allSkills.push(new Skill(4, 'four'));

	});

	it('should be created without error.', () => {
		expect(theService).toBeTruthy();
	});

	it('should correctly filter projects on their name.', done  => {
		theService.reloadProjects('dummy', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(3)
				done();
			}
		})
	});

	it('should correctly filter "ACTIVE" projects on their name and their active status.', done  => {

		// The project with ID 1 and 3 are desactivate
		projectService.allProjects[1].active = false;
		projectService.allProjects[3].active = false;

		theService.reloadProjects('dummy', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(1)
				done();
			}
		})
	});

	it('should correctly handle an empty result.', done  => {
		theService.reloadProjects('unknown', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(0)
				done();
			}
		})
	});
	it('should parse correctly a given criteria containing "skill:java".', ()  => {
		const criteria = theService.parse('skill:java');
		expect (criteria.skill).toBeDefined();
		expect (criteria.skill).toBe('java');
		expect (criteria.name).toBeNull();
	});

	it('should parse correctly a given criteria containing "nope".', ()  => {
		const criteria = theService.parse('nope');
		expect (criteria.skill).toBeNull();
		expect (criteria.name).toBeDefined();
		expect (criteria.name).toBe('nope');
	});
	

	it('should parse correctly a given criteria containing "skill:java;nope".', ()  => {
		const criteria = theService.parse('skill:java;nope');
		expect (criteria.skill).toBeDefined();
		expect (criteria.skill).toBe('java');
		expect (criteria.name).toBeDefined();
		expect (criteria.name).toBe('nope');
	});

	it('should parse correctly a given criteria containing "staff:7".', ()  => {
		const criteria = theService.parse('staff:7');
		expect (criteria.skill).toBeNull();

		expect (criteria.risk).toBeDefined();
		expect (criteria.risk).toBe('staff');
		expect (criteria.riskLevel).toBeDefined();
		expect (criteria.riskLevel).toBe(7);
		
		expect (criteria.name).toBeNull();
	});

	it('should parse correctly a given criteria containing "staff:3;nope".', ()  => {
		const criteria = theService.parse('staff:3;nope');
		expect (criteria.skill).toBeNull();

		expect (criteria.risk).toBeDefined();
		expect (criteria.risk).toBe('staff');
		expect (criteria.riskLevel).toBeDefined();
		expect (criteria.riskLevel).toBe(3);

		expect (criteria.name).toBeDefined();
		expect (criteria.name).toBe('nope');
	});


	it('should correctly filter for a specific skill.', done  => {
		// The project with ID 1 and 3 are desactivate
		projectService.allProjects[2].skills = { 1: new ProjectSkill(1, 100, 100) };
		projectService.allProjects[4].skills = { 1: new ProjectSkill(1, 200, 200) };

		theService.reloadProjects('skill:one', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(2)
				done();
			}
		})
	});

	it('should correctly filter for a specific skill and a specific name.', done  => {
		// The project with ID 1 and 3 are desactivate
		projectService.allProjects[2].skills = { 1: new ProjectSkill(1, 100, 100) };
		projectService.allProjects[4].skills = { 1: new ProjectSkill(1, 200, 200) };

		theService.reloadProjects('skill:one;dummy', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(1)
				done();
			}
		})
	});

	it('should filter projects for a specific level of "staff risk".', done  => {
		
		// The project with ID 1 and 3 are desactivate
		projectService.allProjects[0].staffEvaluation = 4;
		projectService.allProjects[1].staffEvaluation = 2;
		projectService.allProjects[2].staffEvaluation = 3;
		projectService.allProjects[3].staffEvaluation = 4;
		projectService.allProjects[4].staffEvaluation = 1;

		theService.reloadProjects('staff:4', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(2)
				done();
			}
		})
	});

	it('should filter projects for a specific level of "staff risk" and a specific name.', done  => {
		
		// The project with ID 1 and 3 are desactivate
		projectService.allProjects[0].staffEvaluation = 4;
		projectService.allProjects[1].staffEvaluation = 2;
		projectService.allProjects[2].staffEvaluation = 3;
		projectService.allProjects[3].staffEvaluation = 4;
		projectService.allProjects[4].staffEvaluation = 1;

		theService.reloadProjects('staff:4;dummy', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(1)
				done();
			}
		})
	});

	it('should accept all projets with the "*" filter.', done  => {

		theService.reloadProjects('*', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(5)
				done();
			}
		})
	});

	it('should accept all projets with an empty filter.', done  => {
		theService.reloadProjects(null, true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(5)
				done();
			}
		})
	});

	it('should accept all projets with an empty filter.', done  => {
		theService.reloadProjects('', true);
		theService.filteredProjects$.subscribe({
			next: projects => {
				expect(projects.length).toBe(5)
				done();
			}
		})
	});

});
