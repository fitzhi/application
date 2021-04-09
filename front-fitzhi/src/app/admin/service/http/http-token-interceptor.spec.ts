import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpTokenInterceptor } from './http-token-interceptor';
import { DataService } from '../data.service';
import { TokenService } from '../token/token.service';
import { Token } from '../token/token';

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
  
	const httpRequest = httpMock.expectOne(`${DataService.ROOT_URL}/posts`);
  
	expect(httpRequest.request.headers.has('authorization')).toEqual(true);
	expect(httpRequest.request.headers.get('authorization')).toEqual('Bearer access_token');
  });  

  it('should avoid the security header if security is unplugged', () => {
	
	localStorage.setItem('no-security', '1');
	dataService.getPosts().subscribe(response => {
	  expect(response).toBeTruthy();
	});
  
	const httpRequest = httpMock.expectOne(`${DataService.ROOT_URL}/posts`);
	expect(httpRequest.request.headers.has('authorization')).toEqual(false);
  });  
  
});