import { Component, OnInit } from '@angular/core';
import { BackendSetupService } from '../service/backend-setup/backend-setup.service';
import { AuthService } from '../admin/service/auth/auth.service';

@Component({
	selector: 'app-welcome',
	templateUrl: './welcome.component.html',
	styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

	/**
     * Is this the VERY fist launch into fitzhì ?
     * We mention here the VERY FIRST user, who is connecting into Fitzhì.
     */
	veryFirstLaunch = false;

	/**
     * Is this the FIRST launch into fitzhì ?
     * This is not the FIRST user, but this is the first time a user is trtying to connect
     * from this desktop/browser insoide fitzhì.
     */
	firstLaunch = false;

	constructor(
		private backendSetupService: BackendSetupService,
		public authService: AuthService) {
		this.firstLaunch = !this.backendSetupService.hasSavedAnUrl();
	}

	ngOnInit() {
	}

	/**
     * Return `TRUE` if the connection is requested.
	 *
     * There are 2 possible reasons for that :
     * - This is the first connection on fitzhì
     * - The user is not connected for this session
     */
	connectionIsNeeded(): boolean {
		return (!this.authService.isConnected() && !this.firstLaunch);
	}

}
