import { TestBed } from '@angular/core/testing';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TokenService } from '../token/token.service';
import { Token } from '../token/token';

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
			next: token => console.log (token)
		})

		const httpRequest = httpMock.expectOne('myBackendUrl/oauth/token?refresh_token=refresh_token_value&grant_type=refresh_token');
		expect(httpRequest.request.headers.has('authorization')).toEqual(true);

	});

	it('should refresh the authorization token with the refresh token saved in the localstorage', () => {

		tokenService.saveToken(null);		
		localStorage.setItem('refresh_token', 'refresh_token_value');
		localStorage.setItem('access_token', 'access_token_value');

		tokenService.refreshToken$().subscribe({
			next: token => console.log (token)
		})

		const httpRequest = httpMock.expectOne('myBackendUrl/oauth/token?refresh_token=refresh_token_value&grant_type=refresh_token');
		expect(httpRequest.request.headers.has('authorization')).toEqual(true);

	});

	afterEach(() => {
		httpMock.verify();
	});
});