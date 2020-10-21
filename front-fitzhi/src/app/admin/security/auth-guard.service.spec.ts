import { TestBed } from '@angular/core/testing';

import { AuthGuardService } from './auth-guard.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { RouterTestingModule } from '@angular/router/testing';


describe('AuthGuardService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		declarations: [],
		providers: [],
		imports: [
			HttpClientTestingModule,
			RouterTestingModule.withRoutes([]),
		]
}));

	it('should be created', () => {
		const service: AuthGuardService = TestBed.inject(AuthGuardService);
		expect(service).toBeTruthy();
	});
});
