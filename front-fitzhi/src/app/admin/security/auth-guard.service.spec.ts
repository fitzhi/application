import { TestBed } from '@angular/core/testing';

import { AuthGuardService } from './auth-guard.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { RootTestModule } from 'src/app/root-test/root-test.module';


describe('AuthGuardService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		declarations: [],
		providers: [],
		imports: [
			HttpClientTestingModule,
			RouterTestingModule.withRoutes([]),
			RootTestModule
		]
}));

	it('should be created', () => {
		const service: AuthGuardService = TestBed.get(AuthGuardService);
		expect(service).toBeTruthy();
	});
});
