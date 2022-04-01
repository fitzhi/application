import { TestBed } from '@angular/core/testing';
import { GoogleLoginProvider, SocialAuthService, SocialAuthServiceConfig, SocialLoginModule } from 'angularx-social-login';
import { GoogleService } from './google.service';


describe('GoogleService', () => {
	let service: GoogleService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				SocialAuthService,
				
				// Google authentication settings.
				{
					provide: 'SocialAuthServiceConfig',
					useValue: {
						autoLogin: false,
						providers: [
							{
								id: GoogleLoginProvider.PROVIDER_ID,
								provider: new GoogleLoginProvider('690807651852-sqjienqot7ui0pufj4ie4n320pss5ipc.apps.googleusercontent.com')
							}
						]
					} as SocialAuthServiceConfig
				}
			],
			imports: [SocialLoginModule]
		});
		service = TestBed.inject(GoogleService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
