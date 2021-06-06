import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { of } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectSkill } from 'src/app/data/project-skill';
import { Skill } from 'src/app/data/skill';
import { MessageService } from 'src/app/interaction/message/message.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { ProjectFormSkillHandler } from './project-form-skill-handler';

describe('ProjectFormSkillHandler', () => {
	let service: ProjectFormSkillHandler;
	let projectService: ProjectService;
	let skillService: SkillService;
	let messageService: MessageService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			providers: [SkillService, MessageService, ProjectService, ReferentialService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'The revolutionary project');

		skillService = TestBed.inject(SkillService);
		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'One'));
		skillService.allSkills.push(new Skill(2, 'Second one'));

		messageService = TestBed.inject(MessageService);

		service = new ProjectFormSkillHandler(projectService, skillService, messageService);
	});

	it('ADD a skill correctly.', () => {
		const spy = spyOn(service, 'updateSkill').and.returnValue(null);
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'One'} },
		});
		service.addSkill(event);
		expect(projectService.project.skills).toBeTruthy();
		expect(projectService.project.mapSkills.size).toBe(1);
		expect(projectService.project.mapSkills.get(1)).toBeTruthy();
		expect(spy).toHaveBeenCalled();
	});

	it('throw an error if the skill is unknown, when ADDING a skill.', () => {
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'WTF'} },
		});
		expect(function() { service.addSkill(event); }).toThrowError('SEVERE ERROR : Unregistered skill WTF');
	});

	it('is idempotent if we try to add twice the same skill.', () => {
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'One'} },
		});
		service.addSkill(event);
		service.addSkill(event);
		expect(projectService.project.mapSkills.size).toBe(1);
	});

	it('throw an error if the skill is unknown when REMOVING a skill.', () => {
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'WTF'} },
		});
		expect(function() { service.removeSkill(event); }).toThrowError('SEVERE ERROR : Unregistered skill WTF');
	});

	it('REMOVE a skill correctly.', () => {
		projectService.project.mapSkills.set(1, new ProjectSkill(1, 100, 100));
		const spy = spyOn(service, 'updateSkill').and.returnValue(null);
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'One'} },
		});
		service.removeSkill(event);
		expect(projectService.project.skills).toBeTruthy();
		expect(projectService.project.mapSkills.size).toBe(0);
		expect(spy).toHaveBeenCalled();
	});

	it('does not throw an error when removing a skill that does not exist anymore.', () => {
		projectService.project.mapSkills.set(1, new ProjectSkill(1, 100, 100));
		const spy = spyOn(service, 'updateSkill').and.returnValue(null);
		const event: CustomEvent = new CustomEvent('myEvent', {
			bubbles: true,
			composed: true,
			detail: { data: { value: 'One'} },
		});
		service.removeSkill(event);
		expect(projectService.project.skills).toBeTruthy();
		expect(projectService.project.mapSkills.size).toBe(0);
		expect(spy).toHaveBeenCalled();
	});

	it('actualizes the local project, if the update on server is successfull.', () => {
		const spy = spyOn(projectService, 'actualizeProject').and.returnValue(null);
		service.updateSkill(
			1789,
			1,
			function (idProject: number, idSkill: number) { return of(true); });
		expect(spy).toHaveBeenCalled();
	});

	it('does not actualize the local project, if the update on server failed.', () => {
		const spy = spyOn(projectService, 'actualizeProject').and.returnValue(null);
		service.updateSkill(
			1789,
			1,
			function (idProject: number, idSkill: number) { return of(false); });
		expect(spy).not.toHaveBeenCalled();
	});

});
