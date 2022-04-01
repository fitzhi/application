import { Injectable } from '@angular/core';
import { GoogleLoginProvider, SocialAuthService } from 'angularx-social-login';

@Injectable({
	providedIn: 'root'
})
export class GoogleService {

	private promise = this.authService.signIn(GoogleLoginProvider.PROVIDER_ID);

	constructor(private authService: SocialAuthService) { }


	signInWithGoogle(): void {
		this.promise.then(
			(user) => {
				console.log (user)
				console.log (user.firstName)
				console.log (`authToken ${user.authToken}`)
				console.log (`id ${user.id}`)
				console.log (`firstname ${user.firstName}`)
				console.log (`lastname ${user.lastName}`)
				console.log (`token ${user.response.access_token}`)

				/*
				this.tokenService.verifyGoogleToken$(user.idToken).pipe(take(1))
					.subscribe({
						next: res => {
							this.tokenService.token = new Token();
							this.tokenService.token.access_token = user.idToken;
							console.log ("his.tokenService.token.access_token", this.tokenService.token.access_token);
						}
					});
				*/

			},
			(error) => {
				console.log(error);
			}
		)
	}



}
