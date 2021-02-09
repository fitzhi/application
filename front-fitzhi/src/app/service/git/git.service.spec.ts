import { TestBed, TestModuleMetadata, async } from '@angular/core/testing';

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

	it('should connect without error with the repository of Fitzhi.', async(() => {
		expect(service).toBeTruthy();
		service.connect$('https://api.github.com/repos/fitzhi/application')
			.subscribe({
				next: repo => {
					expect(repo.name).toBe('application');
					expect(repo.default_branch).toBe('master');
				}
			});
	}));

	it('should connect handle a wrong repository url.', async(() => {
		expect(service).toBeTruthy();
		service.connect$('https://api.github.com/repos/fitzhi/wrong')
			.subscribe({
				next: repo => {
					expect(repo).toBeNull();
				}
			});
	}));

	it('Testing isGithubUrl(...) with a wrong url.', () => {
		expect(service).toBeTruthy();
		expect(service.isGithubUrl('gitoub.com/fitzhi/test')).toBeFalse();
	});

	it('Testing isGithubUrl(...) with a valid URL.', () => {
		expect(service).toBeTruthy();
		expect(service.isGithubUrl('htpps://www.github.com/fitzhi/application')).toBeTrue();
	});

	it('Testing generateUrlApiGithub(...) with a GITHUB url ', () => {
		expect(service).toBeTruthy();
		expect(service.generateUrlApiGithub('htpps://www.github.com/fitzhi/application')).toBe('https://api.github.com/repos/fitzhi/application');
	});

	it('Testing branches(...) with the Fitzhi GITHUB url.', async(() => {
		expect(service).toBeTruthy();
		service.branches$('https://api.github.com/repos/fitzhi/application/branches', 'master')
			.subscribe({
				next: branches => {
					expect(branches.length).toBe(7);
					expect(branches[0]).toBe('initialization');
					expect(branches[1]).toBe('master');
					expect(branches[2]).toBe('release-1-1');
					expect(branches[3]).toBe('release-1.2');
					expect(branches[4]).toBe('release-1.3');
					expect(branches[5]).toBe('release-1.4');
					expect(branches[6]).toBe('Simple-starting-forms');
				}
			})
	}));

	it('Testing default behavior of branches(...) with the url is wrong.', async(() => {
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
