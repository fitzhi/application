import { TestBed, inject } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SkillService } from './skill.service';
import { of } from 'rxjs';
import { Skill } from 'src/app/data/skill';
import { take } from 'rxjs/operators';

describe('skillService', () => {
	let service: SkillService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [SkillService],
			imports: [HttpClientTestingModule]
		});
		service = TestBed.inject(SkillService);
	});

	it('should be simply created without error', () => {
		expect(service).toBeTruthy();
	});

	it('should CREATE a new skill if skill.id is null', done => {

		const skill = new Skill(null, 'A revolutionary skill');

		// We mock the Skill creation 
		const spyCreateSkill = spyOn(service, 'createSkill$')
			.and.callThrough()
			.and.returnValue(of(new Skill(1789, skill.title)));

		// We mock the Skill creation 
		const spyUpdateSkill = spyOn(service, 'updateSkill$');

		service.save$(skill).pipe(take(1)).subscribe({
			next: skill => {
				expect(skill.id).toBe(1789);
				expect(spyUpdateSkill).not.toHaveBeenCalled();
				done();
			}
		});
		

	});

	it('should UPDATE a skill if skill.id is NOT null', done => {

		const skill = new Skill(1789, 'An empty title');

		// We mock the Skill creation 
		const spyCreateSkill = spyOn(service, 'createSkill$');

		// We mock the Skill creation 
		const spyUpdateSkill = spyOn(service, 'updateSkill$')
			.and.callThrough()
			.and.returnValue(of(new Skill(1789, 'A revolutionaly skill')));


		service.save$(skill).pipe(take(1)).subscribe({
			next: skill => {
				expect(skill.id).toBe(1789);
				expect(skill.title).toBe('A revolutionaly skill');
				expect(spyCreateSkill).not.toHaveBeenCalled();
				done();
			}
		});
		

	});

});
