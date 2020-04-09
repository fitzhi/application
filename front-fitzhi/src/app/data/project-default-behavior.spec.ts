import { TestBed } from '@angular/core/testing';
import { Project } from './project';
import { Skill } from './skill';
import { ProjectSkill } from './project-skill';


describe('Testing the simple Project object', () => {

	beforeEach(() => TestBed.configureTestingModule({
		imports: []
	}));

	it('Should be created', () => {
		const project = new Project();
		expect(project).toBeTruthy();

		expect(project.mapSkills).toBeDefined();

		const projectSkill = new ProjectSkill(1, 0, 0);
		project.mapSkills.set(1, projectSkill);

		expect(project.mapSkills.size).toBe(1);
	});

});
