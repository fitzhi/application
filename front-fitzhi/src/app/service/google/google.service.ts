import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { OpenidServer } from 'src/app/data/openid-server';
import { traceOn } from 'src/app/global';
import jwt_decode from 'jwt-decode';
import { HttpClient } from '@angular/common/http';

declare var google: any;

class GoogleToken {
	sub: string;
	given_name: string;
	family_name: string;
	name: string;
	email: string;
}

@Injectable({
	providedIn: 'root'
})
export class GoogleService {

	public GOOGLE_SERVER_ID = 'GOOGLE';

	private registeredSubject$ = new BehaviorSubject<boolean>(false); 
	public isRegistered$ = this.registeredSubject$.asObservable();

	private authenticatedSubject$ = new BehaviorSubject<boolean>(false); 
	public isAuthenticated$ = this.authenticatedSubject$.asObservable();

	public clientId: string;

	/**
	 * The raw JWT retrieved from Google.
	 */
	 public jwt = null;

	/**
	 * The decoded authentication token
	 */
	public googleToken: GoogleToken = null;

	private googleClientLoadedSubject$ = new BehaviorSubject<boolean>(false);
	private googleClientLoaded$ = this.googleClientLoadedSubject$.asObservable();

	constructor(private httpClient: HttpClient) { }

	public register() {
		this.registeredSubject$.next(true);
	}

	/**
	 * Take in account the Google openId server settings if the Google has been registered.
	 * @param servers the authentification servers loaded fron the backend.
	 */
	takeInAccountDeclaredServers(servers: OpenidServer[]) {
		const serverGoogle = servers.find(server => server.serverId === this.GOOGLE_SERVER_ID);
		if (serverGoogle) {
			if (traceOn()) {
				console.log ('Google oauth server found with %s as clientId', serverGoogle.clientId);
			}
			this.clientId = serverGoogle.clientId;
			this.register();
		}
	}

	/**
	 * Initialize the Google workplace.
	 * 
	 * @param document the current HTML document
	 */
	initialize (document) {

		if (traceOn()) {
			console.log ('Initializing googleService...');
		}

		const handleCredentialResponse = (response: any) => {
			const data = {idToken: response.credential, oauth: 'v3'};
			this.loginCheckSocial(data.idToken);
		};

		const id = 'google-client-script';

		const script = document.getElementById(id);
		if (script === null) {

			const crscript = document.createElement('script');
			crscript.setAttribute('src', 'https://accounts.google.com/gsi/client');
			crscript.setAttribute('id', id);
			// crscript.setAttribute('async', '');
			document.body.appendChild(crscript);

			crscript.onload = () => {
				this.googleClientLoadedSubject$.next(true);
				if (traceOn()) {
					console.log ('...googleService is initialized');
				}	
				google.accounts.id.initialize({
					client_id: this.clientId,
					callback: handleCredentialResponse
				});
				google.accounts.id.renderButton(document.getElementById('btnGoogle'), {theme: 'outline', size: 'large'});
			};

		} else {
			this.googleClientLoaded$.subscribe({
				next: loaded => {
					if (loaded) {
						google.accounts.id.initialize({
							client_id: this.clientId,
							callback: handleCredentialResponse
						});
					}
				}
			})
		}
	}

	public signIn() {
		this.authenticatedSubject$.next(true);
	}

	/**
	 * Display the Google connection dialog.
	 */
	public render() {
		google.accounts.id.prompt();
	}

	public isLoggedin() {
		console.log ('isLoggedin()');
	}

	/**
	 * Decode and take in account the JWT token received.
	 * @param jwt the Json Web Token
	 */
	public loginCheckSocial (jwt: any) {
		this.googleToken = jwt_decode(jwt) as GoogleToken;
		this.jwt = jwt;
		if (traceOn()) {
			console.log ('login %s %s %s @', this.googleToken.sub, this.googleToken.given_name, this.googleToken.family_name, this.googleToken.email);
		}
		this.signIn();
	}

}
