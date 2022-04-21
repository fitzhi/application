import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { OpenidServer } from 'src/app/data/openid-server';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class GithubService {

	public GITHUB_SERVER_ID = 'GITHUB';

	private registeredSubject$ = new BehaviorSubject<boolean>(false);
	public isRegistered$ = this.registeredSubject$.asObservable();

	private authenticatedSubject$ = new BehaviorSubject<boolean>(false);
	public isAuthenticated$ = this.authenticatedSubject$.asObservable();

	public clientId: string;

	constructor() { }

	public register() {
		this.registeredSubject$.next(true);
	}

	/**
	 * Take in account the Github identity server if Github has been registered.
	 * @param servers the authentification servers loaded fron the backend.
	 */
	takeInAccountDeclaredServers(servers: OpenidServer[]) {
		const serverGithub = servers.find(server => server.serverId === this.GITHUB_SERVER_ID);
		if (serverGithub) {
			if (traceOn()) {
				console.log ('Github oauth server found with %s as clientId', serverGithub.clientId);
			}
			this.clientId = serverGithub.clientId;
			this.register();
		}
	}

	render() {
		console.log ("render");
	}

}
