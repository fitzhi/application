import { Injectable } from '@angular/core';
import { Settings } from '../data/settings';
import { switchMap, map, catchError } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { InternalService } from '../internal-service';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { Constants } from '../constants';
import { empty, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class SonarService extends InternalService {

	/**
	 * Version of Sonar.
	 */
	sonarVersion: string;

	/**
	 * URL of Sonar.
	 */
	urlSonar: string;

	/**
	 * TRUE if we cannot access the SONAR;
	 */
	sonarOn = false;

	constructor(
		private httpClient: HttpClient,
		private backendSetupService: BackendSetupService) {
		super();
	}

	loadSonarVersion() {

		console.log ('nope')
		this.httpClient
			.get(this.backendSetupService.url() + '/admin/settings')
				.pipe(switchMap( (settings: Settings) => {
					return this.httpClient
						.get(settings.urlSonar + '/api/server/version',
								{responseType: 'text' as 'json'})
						.pipe (
							map( (version: string) => {
								return {
									settings: settings,
									sonarOn: true,
									version: version
								};
							}),
							catchError( (error, caught$)  => {
								console.log ('error', error);
								return of( {settings: settings, sonarOn: false, version: ''});
							})
					); }))
				.subscribe(
					data => {
						this.sonarVersion = data.version;
						this.urlSonar =  data.settings.urlSonar;
						this.sonarOn = data.sonarOn;
						if (this.sonarOn) {
							console.log('Sonar version ' + data.version + ' installed at the URL ' + data.settings.urlSonar);
						} else {
							console.log('Sonar is OFFLINE  at the URL ' + data.settings.urlSonar);
						}
					},
					error => console.log (error)
				);
	}

}
