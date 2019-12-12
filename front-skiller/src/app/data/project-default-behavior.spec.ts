import { TestBed } from '@angular/core/testing';
import { Project } from './project';
import { Skill } from './skill';


describe('Testing the simple Project object', () => {

	beforeEach(() => TestBed.configureTestingModule({
		imports: []
	}));

	it('Should be created', () => {
		const project = new Project();
		expect(project).toBeTruthy();
		expect(project.skills).toBeDefined();
		const skill = new Skill();
		skill.id = 1;
		skill.title = 'my Title';
		project.skills.push(skill);
		expect(project.skills.length).toBe(1);
	});

});
