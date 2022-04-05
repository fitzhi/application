import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { OpenidServer } from 'src/app/data/openid-server';
import { traceOn } from 'src/app/global';
import jwt_decode from "jwt-decode";
import { StringMapWithRename } from '@angular/compiler/src/compiler_facade_interface';

declare var google: any;

class GoogleToken {
	jti: string;
	given_name: string;
	family_name: string;
	name: string;
	email: string;
}

@Injectable({
	providedIn: 'root'
})
export class GoogleService {

	public GOOGLE_SERVER_ID = "GOOGLE";

	public isRegistered$ = new BehaviorSubject<boolean>(false);

	public clientId: string;

	constructor() { }

	/**
	 * Take in account the Google openId server settings if the Google has been registered.
	 * @param servers the authentification servers loaded fron the backend.
	 */
	takeInAccountDeclaredServers(servers: OpenidServer[]) {
		const server = servers.find(server => server.serverId === this.GOOGLE_SERVER_ID);
		if (server) {
			if (traceOn()) {
				console.log ('Google oauth server found with %s as clientId', server.clientId)
			}
			this.clientId = server.clientId;
			this.isRegistered$.next(true);
		}
	}

	initialize(document) {
		let handleCredentialResponse = (response:any) => {
			const data = {idToken:response.credential,oauth:'v3'};
			this.loginCheckSocial(data.idToken);
		}
		
		let id = 'google-client-script';
		
		let script = document.getElementById(id);
		if (script === null) {
			
			let crscript = document.createElement('script');			
			crscript.setAttribute('src', 'https://accounts.google.com/gsi/client');
			crscript.setAttribute('id', id);
			crscript.setAttribute('async', '');
			document.body.appendChild(crscript);
			
			crscript.onload = () => {
				google.accounts.id.initialize({
					client_id: this.clientId,
					callback: handleCredentialResponse
				});
			
				google.accounts.id.renderButton(document.getElementById("btnGoogle"),{ theme: "outline", size: "large" });
			}
			
		} else {
			google.accounts.id.initialize({
				client_id: this.clientId,
				callback: handleCredentialResponse
			});	
		}
	}

	/**
	 * Display the Google connection dialog.
	 */
	public render() {
		google.accounts.id.prompt();
	}

	public isLoggedin(){
		console.log ('isLoggedin()');
	}

	/**
	 * 
	 * @param data 
	 */
	public loginCheckSocial(data: any) {
		console.log (jwt_decode(data));
		let token = jwt_decode(data) as GoogleToken;
		if (traceOn()) {
			console.log ("login %s %s %s @", token.jti, token.given_name, token.family_name, token.email);
		}
	}

	/*
	public signInWithGoogle(): void {
		this.authService.initState.subscribe({
			next: value => {
			this.authService.signIn(GoogleLoginProvider.PROVIDER_ID).then((user) => {

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
				*

			},
			(error) => {
				console.log(error);
			}
		);
	}})

	} */

}
