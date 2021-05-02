import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';
import { ReferentialService } from '../../../service/referential.service';
import { CinematicService } from '../../../service/cinematic.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
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

	it('should call the search with the previous criteria.', ()  => {

		theService.currentSearch.criteria = 'dummy';
		theService.currentSearch.activeOnly = true;
		theService.currentSearch.done = true;

		const spy = spyOn(theService, 'search').withArgs('dummy', true).and.returnValue();
		theService.reloadProjects();

		expect(spy).toHaveBeenCalled();
	});

	it('should NOT call the search function without previous search.', ()  => {

		theService.currentSearch.done = false;

		const spy = spyOn(theService, 'search');
		theService.reloadProjects();

		expect(spy).not.toHaveBeenCalled();
	});

});
