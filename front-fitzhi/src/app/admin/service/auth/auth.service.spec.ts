import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('AuthService', () => {
	beforeEach(() => TestBed.configureTestingModule({
		declarations: [],
		providers: [],
		imports: [
			HttpClientTestingModule
		]
	}));

	it('should be created', () => {
		const service: AuthService = TestBed.inject(AuthService);
		expect(service).toBeTruthy();
	});
});
