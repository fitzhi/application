import { TestBed } from '@angular/core/testing';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { HttpHeaders, HTTP_INTERCEPTORS } from '@angular/common/http';
import { DataService } from '../data.service';
import { TokenService } from '../token/token.service';
import { HttpRefreshTokenErrorInterceptor } from './http-refresh-token-error-interceptor';
import { Token } from '../token/token';
import { of } from 'rxjs';
import { MessageService } from 'src/app/interaction/message/message.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { callbackify } from 'util';

describe(`HttpRefreshTokenErrorInterceptor`, () => {
	let httpMock: HttpTestingController;
	let dataService: DataService;
	let router: Router;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				HttpClientTestingModule,
				RouterTestingModule.withRoutes([])
			],
			providers: [
				DataService,
				MessageService,
				{
					provide: HTTP_INTERCEPTORS,
					useClass: HttpRefreshTokenErrorInterceptor,
					multi: true,
				},
			],
		});

		httpMock = TestBed.inject(HttpTestingController);
		dataService = TestBed.inject(DataService);
		router = TestBed.inject(Router);

	});

	it('should redirect the application to "/login" if an "Invalid refresh" error is emitted', () => {

		const navigateSpy = spyOn(router, 'navigate');

		dataService.ROOT_URL = 'https://urlWith401Error';

		callDataService();

		const req = httpMock.expectOne('https://urlWith401Error/posts');
		const error = new ErrorEvent('error');

		const httpHeaders = new HttpHeaders()
			.append('www-authenticate', 'Bearer error="invalid_token", error_description="Invalid refresh token (expired):');
		req.error(
			error,
			{
				headers: httpHeaders,
				status: 401,
				statusText: 'Unauthorized!',
			});
		expect(navigateSpy).toHaveBeenCalledWith(['/login']);
	});

	it('should redirect the application to "/login" if a "Full authentication is required" error is emitted', () => {

		const navigateSpy = spyOn(router, 'navigate');

		dataService.ROOT_URL = 'https://urlWith401Error';

		callDataService();

		const req = httpMock.expectOne('https://urlWith401Error/posts');
		const error = new ErrorEvent('error');

		const httpHeaders = new HttpHeaders()
			.append('WWW-Authenticate', 'Bearer realm="my_rest_api", error="unauthorized", error_description="Full authentication is required to access this resource"');
		req.error(
			error,
			{
				headers: httpHeaders,
				status: 401,
				statusText: 'Unauthorized!',
			});
		expect(navigateSpy).toHaveBeenCalledWith(['/login']);
	});

	it('should NOT redirect the application to "/login" if ONLY the access token has expired', () => {

		const navigateSpy = spyOn(router, 'navigate');

		callDataService();

		const req = httpMock.expectOne('https://urlWith401Error/posts');
		const error = new ErrorEvent('error');

		const httpHeaders = new HttpHeaders()
			.append('WWW-Authenticate', 'Bearer realm="my_rest_api", error="invalid_token", error_description="Access token expired');
		req.error(
			error,
			{
				headers: httpHeaders,
				status: 401,
				statusText: 'Unauthorized!',
			});
		expect(navigateSpy).not.toHaveBeenCalledWith(['/login']);
	});

	function callDataService() {
		dataService.ROOT_URL = 'https://urlWith401Error';

		dataService.getPosts().subscribe({
			next: response => {
				expect(response).toBeTruthy();
			},
			error: error => { console.log ('error catched', error); }
		});
	}

	afterEach(() => {
		httpMock.verify();
	});
});
