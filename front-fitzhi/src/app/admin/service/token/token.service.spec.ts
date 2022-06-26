import { TestBed } from '@angular/core/testing';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TokenService } from '../token/token.service';
import { Token } from '../token/token';
import { HttpRequest } from '@angular/common/http';

describe(`TokenService`, () => {
	let httpMock: HttpTestingController;
	let tokenService: TokenService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [
				TokenService,
			],
		});

		httpMock = TestBed.inject(HttpTestingController);
		tokenService = TestBed.inject(TokenService);

		localStorage.removeItem('no-security');
		localStorage.setItem('backendUrl', 'myBackendUrl');
		localStorage.removeItem('refresh_token');
		localStorage.removeItem('access_token');

	});

	it('should refresh the authorization token with the given refresh token', () => {

		const token = new Token();
		token.refresh_token = 'refresh_token_value';
		tokenService.saveToken(token);

		tokenService.refreshToken$().subscribe({
			next: tk => console.log (tk)
		});

		const httpRequest = httpMock.expectOne('myBackendUrl/oauth/token?refresh_token=refresh_token_value&grant_type=refresh_token');
		expect(httpRequest.request.headers.has('authorization')).toEqual(true);

	});

	it('should refresh the authorization token with the refresh token saved in the localstorage', () => {

		tokenService.saveToken(null);
		localStorage.setItem('refresh_token', 'refresh_token_value');
		localStorage.setItem('access_token', 'access_token_value');

		tokenService.refreshToken$().subscribe({
			next: token => console.log (token)
		});

		const httpRequest = httpMock.expectOne('myBackendUrl/oauth/token?refresh_token=refresh_token_value&grant_type=refresh_token');
		expect(httpRequest.request.headers.has('authorization')).toEqual(true);

	});

	it('should extract correctly the grant_type from the request if any.', () => {
		const req = new HttpRequest(
			'POST',
			'url',
			'user:myUser&password:MyPassword&grant_type:password');
		const grant = tokenService.grant_type(req);
		expect(grant).toBe('password');
	});

	it('should handle correctly a request without grant_type.', () => {
		const req = new HttpRequest(
			'POST',
			'url',
			'Nope...');
		const grant = tokenService.grant_type(req);
		expect(grant).toBeNull();
	});

	it('should handle correctly a request without body to extract the grant_type.', () => {
		const req = new HttpRequest(
			'POST',
			'url',
			null);
		const grant = tokenService.grant_type(req);
		expect(grant).toBeNull();
	});

	afterEach(() => {
		localStorage.removeItem('backendUrl');
		httpMock.verify();
	});
});
