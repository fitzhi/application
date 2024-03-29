import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageService } from 'src/app/interaction/message/message.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { SunburstCinematicService } from 'src/app/tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { CinematicService } from '../cinematic.service';
import { FileService } from '../file.service';
import { ProjectService } from '../project/project.service';
import { ReferentialService } from '../referential/referential.service';
import { GithubService } from './github.service';


describe('GithubService', () => {
	let service: GithubService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			declarations: [],
			providers: [ProjectService, ReferentialService, SkillService, FileService, MessageService, SunburstCinematicService,
				BackendSetupService, CinematicService],
			imports: [HttpClientTestingModule, HttpClientModule, MatDialogModule]
		});
		service = TestBed.inject(GithubService);
	});

	it('should be successfully created.', () => {
		expect(service).toBeTruthy();
	});

	it('take in account an empty array of server retrieved from the backend.', done => {
		expect(service).toBeTruthy();
		expect(service.clientId).toBeUndefined();
		service.takeInAccountDeclaredServers ([]);
		expect(service.clientId).toBeUndefined();
		service.isRegistered$.subscribe({
			next: isRegistered => {
				expect(isRegistered).toBeFalse();
				done();
			}
		});
	});

	it('take in account the "Github" server declared in the backend.', done => {
		expect(service).toBeTruthy();
		expect(service.clientId).toBeUndefined();
		service.takeInAccountDeclaredServers (
			[
				{
					serverId: 'GITHUB',
					clientId: 'theclientIdGithub.com'
				},
				{
					serverId: 'Nope',
					clientId: 'N/A.com'
				}
			]);
		expect(service.clientId).toBe('theclientIdGithub.com');
		service.isRegistered$.subscribe({
			next: isRegistered => {
				expect(isRegistered).toBeTrue();
				done();
			}
		});
	});

	it('do not take in account the "Github" server, if it is not declared in the backend.', done => {
		expect(service).toBeTruthy();
		expect(service.clientId).toBeUndefined();
		service.takeInAccountDeclaredServers (
			[
				{
					serverId: 'NOT_GITHUB',
					clientId: 'theclientIdGithub.com'
				},
				{
					serverId: 'Nope',
					clientId: 'N/A.com'
				}
			]);
		expect(service.clientId).toBeUndefined();
		service.isRegistered$.subscribe({
			next: isRegistered => {
				expect(isRegistered).toBeFalse();
				done();
			}
		});
	});




});
