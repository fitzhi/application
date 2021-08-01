import { HTTP_INTERCEPTORS } from '@angular/common/http';
import {
	HttpClientTestingModule,
	HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DataService } from '../data.service';
import { Token } from '../token/token';
import { TokenService } from '../token/token.service';
import { HttpTokenInterceptor } from './http-token-interceptor';

describe(`AuthHttpInterceptor`, () => {
	let httpMock: HttpTestingController;
	let dataService: DataService;
	let tokenService: TokenService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [
				DataService,
				TokenService,
				{
					provide: HTTP_INTERCEPTORS,
					useClass: HttpTokenInterceptor,
					multi: true,
				},
			],
		});

		httpMock = TestBed.inject(HttpTestingController);
		dataService = TestBed.inject(DataService);
		tokenService = TestBed.inject(TokenService);

		localStorage.removeItem('no-security');

	});

	it('should add an authorization header in nominal mode', () => {
		const token = new Token();
		token.access_token = 'access_token';
		tokenService.saveToken(token);

		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);

		expect(httpRequest.request.headers.has('authorization')).toEqual(true);
		expect(httpRequest.request.headers.get('authorization')).toEqual('Bearer access_token');
	});

	it('should avoid to add the authorization header if security is unplugged', () => {

		localStorage.setItem('no-security', '1');
		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);
		expect(httpRequest.request.headers.has('authorization')).toEqual(false);
	});

	it('should avoid to add the authorization header before user registration', () => {

		dataService.ROOT_URL = 'https://localhost:8080/api/admin/isVeryFirstConnection';
		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);
		expect(httpRequest.request.headers.has('authorization')).toEqual(false);
	});

	it('should avoid to add the authorization header for Github', () => {

		dataService.ROOT_URL = 'https://api.github.com/nope';
		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);
		expect(httpRequest.request.headers.has('authorization')).toEqual(false);
	});

	it('should handle correctly a 401 error', () => {

		dataService.ROOT_URL = 'https://urlWith401Error';

		const token = new Token();
		token.access_token = 'updated_access_token';
		const spy = spyOn(tokenService, 'refreshToken$').and.returnValue(of(token));

		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const req = httpMock.expectOne('https://urlWith401Error/posts');
		req.error(new ErrorEvent('error'), { status: 401, statusText: 'Unauthorized resource' });

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);
		expect(httpRequest.request.headers.has('authorization')).toEqual(true);
		expect(httpRequest.request.headers.get('authorization')).toEqual('Bearer updated_access_token');
	});

	afterEach(() => {
		httpMock.verify();
	});
});
