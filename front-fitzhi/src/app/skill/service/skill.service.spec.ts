import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { Skill } from 'src/app/data/skill';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { SkillService } from './skill.service';


describe('skillService', () => {
	let service: SkillService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [SkillService, BackendSetupService],
			imports: [HttpClientTestingModule],
			declarations: []
		});
		service = TestBed.inject(SkillService);

		httpTestingController = TestBed.inject(HttpTestingController);
		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('TEST_URL');

	});

	it('should be simply created without error.', () => {
		expect(service).toBeTruthy();
	});

	it('should CREATE a new skill if skill.id is null.', done => {

		const skill = new Skill(null, 'A revolutionary skill');

		// We mock the Skill creation
		const spyCreateSkill = spyOn(service, 'createSkill$')
			.and.callThrough()
			.and.returnValue(of(new Skill(1789, skill.title)));

		// We mock the Skill update
		const spyUpdateSkill = spyOn(service, 'updateSkill$');

		service.save$(skill).pipe(take(1)).subscribe({
			next: sk => {
				expect(sk.id).toBe(1789);
				expect(spyUpdateSkill).not.toHaveBeenCalled();
				done();
			}
		});
	});

	it('should UPDATE a skill if skill.id is NOT null.', done => {

		const skill = new Skill(1789, 'An empty title');

		// We mock the Skill creation
		const spyCreateSkill = spyOn(service, 'createSkill$');

		// We mock the Skill update
		const spyUpdateSkill = spyOn(service, 'updateSkill$')
			.and.callThrough()
			.and.returnValue(of(new Skill(1789, 'A revolutionaly skill')));

		service.save$(skill).pipe(take(1)).subscribe({
			next: sk => {
				expect(sk.id).toBe(1789);
				expect(sk.title).toBe('A revolutionaly skill');
				expect(spyCreateSkill).not.toHaveBeenCalled();
				done();
			}
		});
	});

	it('should inform the system that the skills collection has been updated.', done => {

		service.setAllSkills([new Skill(1789, 'the french revolution')]);

		service.allSkillsLoaded$.pipe(take(1)).subscribe({
			next: doneAndOk => expect(doneAndOk).toBeTrue(),
			complete: () =>	done()
		});
	});

	it('should load the skills from the backend server.', done => {

		const req = httpTestingController.expectOne('TEST_URL/api/skill');
		expect(req.request.method).toBe('GET');
		req.flush([]);

		service.loadSkills();
		
		service.allSkillsLoaded$.pipe(take(1)).subscribe({
			next: doneAndOk => expect(doneAndOk).toBeTrue(),			
			complete: () =>	done()
		});
	});

	it('should retrieve the id of a skill from its title.', () => {
		service.allSkills = [];
		service.allSkills.push(new Skill(1, 'one'));
		service.allSkills.push(new Skill(2, 'two'));

		expect(service.id('one')).toBe(1);
		expect(service.id('ONE')).toBe(-1);
		expect(service.id('three')).toBe(-1);
		expect(service.id('two')).toBe(2);
	});

});
