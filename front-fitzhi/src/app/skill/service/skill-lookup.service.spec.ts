import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
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

	});

	it('should lookup a skill with its title from the backend server.', done => {

		httpTestingController = TestBed.inject(HttpTestingController);
		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('TEST_URL');

		service.lookup$('one').pipe(take(1)).subscribe({
			next: skill => {
				expect(skill.id).toBe(1);
				expect(skill.title).toBe('one');
				done();
			}
		});
/*
		const req2 = httpTestingController.expectOne('TEST_URL/api/skill');
		expect(req2.request.method).toBe('GET');
		req2.flush([]);
*/
		const req1 = httpTestingController.expectOne('TEST_URL/api/skill/name/one');
		expect(req1.request.method).toBe('GET');
		req1.flush(new Skill(1, 'one'));

	});

});
