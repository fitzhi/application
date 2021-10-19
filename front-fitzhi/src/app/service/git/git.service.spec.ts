import { TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';

import { GitService } from './git.service';
import { HttpClientModule } from '@angular/common/http';
import { subscribeOn } from 'rxjs/operators';

describe('GitService', () => {
	let service: GitService;

	beforeEach(async () => {

		const testConf: TestModuleMetadata = {
			declarations: [],
			providers: [],
			imports: [HttpClientModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();

	});

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(GitService);
	});

	it('should connect without error with the repository of Fitzhi.', waitForAsync(() => {
		expect(service).toBeTruthy();
		service.connect$('https://api.github.com/repos/fitzhi/application')
			.subscribe({
				next: repo => {
					expect(repo.name).toBe('application');
					expect(repo.default_branch).toBe('master');
				}
			});
	}));

	it('should handle a wrong repository url.', waitForAsync(() => {
		expect(service).toBeTruthy();
		service.connect$('https://api.github.com/repos/fitzhi/wrong')
			.subscribe({
				next: repo => {
					expect(repo).toBeNull();
				}
			});
	}));

	it('should handle correctly the method isGithubUrl(...) with a wrong url.', () => {
		expect(service).toBeTruthy();
		expect(service.isGithubUrl('gitoub.com/fitzhi/test')).toBeFalse();
	});

	it('should handle correctly the method with a valid URL.', () => {
		expect(service).toBeTruthy();
		expect(service.isGithubUrl('htpps://www.github.com/fitzhi/application')).toBeTrue();
	});

	it('should handle correctly the method generateUrlApiGithub(...) with a GITHUB url ', () => {
		expect(service).toBeTruthy();
		expect(service.generateUrlApiGithub('htpps://www.github.com/fitzhi/application')).toBe('https://api.github.com/repos/fitzhi/application');
	});

	it('should handle correctly the method branches(...) with the Fitzhi GITHUB url.', waitForAsync(() => {
		expect(service).toBeTruthy();
		service.branches$('https://api.github.com/repos/fitzhi/application/branches', 'master')
			.subscribe({
				next: branches => {
					expect((branches.length > 8)).toBeTruthy();
					expect(branches.indexOf('initialization') > -1).toBeTrue();
					expect(branches.indexOf('master') > -1).toBeTrue();
					expect(branches.indexOf('release-1-1') > -1).toBeTrue();
					expect(branches.indexOf('release-1.2') > -1).toBeTrue();
					expect(branches.indexOf('release-1.3') > -1).toBeTrue();
					expect(branches.indexOf('release-1.4') > -1).toBeTrue();
					expect(branches.indexOf('release-1.5') > -1).toBeTrue();
					expect(branches.indexOf('release-1.6') > -1).toBeTrue();
					expect(branches.indexOf('Simple-starting-forms') > -1).toBeTrue();
				}
			});
	}));

	it('should handle correctly the method branches(...) with the url is wrong.', waitForAsync(() => {
		expect(service).toBeTruthy();
		service.branches$('https://api.github.com/repos/fitzhi/application/wrong', 'master')
			.subscribe({
				next: branches => {
					expect(branches.length).toBe(1);
					expect(branches[0]).toBe('master');
				}
			});
	}));

});
