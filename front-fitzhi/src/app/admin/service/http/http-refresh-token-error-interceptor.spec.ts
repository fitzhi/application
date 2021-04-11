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

describe(`HttpRefreshTokenErrorInterceptor`, () => {
	let httpMock: HttpTestingController;
	let dataService: DataService;
	let tokenService: TokenService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				HttpClientTestingModule,
				RouterTestingModule.withRoutes([])
			],
			providers: [
				DataService,
				TokenService,
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
		tokenService = TestBed.inject(TokenService);

	});


// NO TEST. IT DOES NOT WORK !!! WHY ?????

/*
	it('should handle correctly a 401 error', () => {

		dataService.ROOT_URL = 'https://urlWith401Error';

		const token = new Token();
		token.access_token = 'updated_access_token';
		const spy = spyOn(tokenService, 'refreshToken$').and.returnValue(of(token));

		dataService.getPosts().subscribe(response => {
			expect(response).toBeTruthy();
		});

		const req = httpMock.expectOne('https://urlWith401Error/posts');
		const errorInitEvent: ErrorEventInit = {
			error : 'invalid token',
		};
		const error = new ErrorEvent('error', errorInitEvent);

		// Why does the httpHeaders not contain www-authenticate value ? 
		const httpHeaders = new HttpHeaders()
			.append('www-authenticate', 'Bearer error="invalid_token", error_description="Invalid refresh token (expired):');
		console.log ('nope...', httpHeaders);
		req.error( 
			error,
			{	
				headers: httpHeaders,
				status: 401, 
				statusText: 'Unauthorized!', 
			});
		/*
		req.flush(
			{
				type: 'ERROR',
				status: 401,
				statusText: 'Unauthorized',
				body: 
					{ 
						error: 'invalid_token', 
						error_description: 'Invalid refresh token (expired): 6be72b48-88e5-4442-83f7-46de290fb644'
					},
			}
		);
		*

		const httpRequest = httpMock.expectOne(`${dataService.ROOT_URL}/posts`);
	});
*/
	afterEach(() => {
		httpMock.verify();
	});
});