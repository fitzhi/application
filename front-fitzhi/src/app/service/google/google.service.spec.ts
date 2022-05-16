import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { GoogleService } from './google.service';


describe('GoogleService', () => {
	let service: GoogleService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [],
			imports: [HttpClientTestingModule]
		});
		service = TestBed.inject(GoogleService);
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

	it('take in account the Google server declared in the backend.', done => {
		expect(service).toBeTruthy();
		expect(service.clientId).toBeUndefined();
		service.takeInAccountDeclaredServers (
			[
				{
					serverId: 'GOOGLE',
					clientId: 'theclientIdGoogle.com'
				},
				{
					serverId: 'Nope',
					clientId: 'N/A.com'
				}
			]);
		expect(service.clientId).toBe('theclientIdGoogle.com');
		service.isRegistered$.subscribe({
			next: isRegistered => {
				expect(isRegistered).toBeTrue();
				done();
			}
		});
	});

	it('do not take in account the Google server if it is not declared in the backend.', done => {
		expect(service).toBeTruthy();
		expect(service.clientId).toBeUndefined();
		service.takeInAccountDeclaredServers (
			[
				{
					serverId: 'NOT_GOOGLE',
					clientId: 'theclientIdGoogle.com'
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
