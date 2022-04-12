import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Token } from '../token/token';
import { TokenService } from '../token/token.service';
import { OpenIdCredentials } from 'src/app/data/open-id-credentials';
import { Collaborator } from 'src/app/data/collaborator';

describe('AuthService', () => {
	let httpTestingController: HttpTestingController;
	let tokenService: TokenService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			declarations: [],
			providers: [],
			imports: [
				HttpClientTestingModule
			]
		}).compileComponents();
		httpTestingController = TestBed.inject(HttpTestingController);
		tokenService = TestBed.inject(TokenService);
		localStorage.setItem('backendUrl', 'URL_OF_SERVER');
	});

	it('should be created.', () => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();
	});

	it('should handle correctly a CLASSIC successful connection.', done => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();

		const spy = spyOn(tokenService, 'saveToken').and.returnValue(null);

		service.connectClassic$('my-user', 'my-password').subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBeTrue();
				done();
			}
		});

		const req = httpTestingController.expectOne('URL_OF_SERVER/oauth/token');
		expect(req.request.method).toBe('POST');
		expect(req.request.body).toBe('username=my-user&password=my-password&grant_type=password'); // This is not a credential. //NOSONAR
		const t = new Token();
		t.access_token = '1234';
		t.refresh_token = '5678';
		req.flush(t);

		expect(spy).toHaveBeenCalled();
	});

	it('should handle correctly a CLASSIC connection failure.', done => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();

		const spySaveToken = spyOn(tokenService, 'saveToken').and.returnValue(null);

		service.connectClassic$('my-user', 'my-password').subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBeFalse();
				done();
			}
		});

		const req = httpTestingController.expectOne('URL_OF_SERVER/oauth/token');
		expect(req.request.method).toBe('POST');
		expect(req.request.body).toBe('username=my-user&password=my-password&grant_type=password'); // This is not a credential. //NOSONAR
		req.error(new ErrorEvent('error'), { status: 500, statusText: 'Invalid login/password!' });

		expect(spySaveToken).not.toHaveBeenCalled();
	});

	it('should handle correctly an OPENID successful connection.', done => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();

		const spy = spyOn(service, 'setConnect').and.returnValue(null);

		service.connectOpenId$(new OpenIdCredentials('GOOGLE', 'google-jwt')).subscribe({
			next: st => {
				expect(st.idStaff).toBe(1789);
				expect(st.lastName).toBe('VIDAL');
				done();
			}
		});

		const req = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/connect');
		expect(req.request.method).toBe('POST');
		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.firstName = 'Frédéric';
		staff.lastName = 'VIDAL';
		req.flush(staff);

		expect(spy).toHaveBeenCalled();
	});

	it('should handle correctly an OPENID connection failure.', done => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();

		const spy = spyOn(service, 'setDisconnect').and.returnValue(null);

		service.connectOpenId$(new OpenIdCredentials('GOOGLE', 'google-jwt')).subscribe({
			next: staff => null,
			error: err => {
				console.log ('error', err);
				done();
			}
		});

		const req = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/connect');
		expect(req.request.method).toBe('POST');
		const error = new ErrorEvent('error');
		req.error(
			error,
			{
				status: 404,
				statusText: 'Not found!',
			});


		expect(spy).toHaveBeenCalled();
	});

});
