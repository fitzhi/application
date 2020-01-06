import { Component, OnInit } from '@angular/core';
import { BackendSetupService } from '../service/backend-setup/backend-setup.service';
import { AuthService } from '../admin/service/auth/auth.service';
import { StaffListService } from '../staff-list-service/staff-list.service';
import { Collaborator } from '../data/collaborator';
import { Slice } from './pie-dashboard/slice';
import { BehaviorSubject } from 'rxjs';

@Component({
	selector: 'app-welcome',
	templateUrl: './welcome.component.html',
	styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

	/**
     * Is this the VERY fist launch into WIBKAC ?
     * We speak about the FIRST user connecting for the FIRST time into Wibkac ?
     */
	veryFirstLaunch = false;

	/**
     * Is this the FIRST launch into WIBKAC ?
     * This is not the FIRST user, but this is the first time a user is trtying to connect
     * from this desktop/browser insoide Wibkac.
     */
	firstLaunch = false;

	/**
	 * Observable emetting the configuration of the pie.
	 */
	private slices$ = new BehaviorSubject<Slice[]>([]);

	constructor(
		private backendSetupService: BackendSetupService,
		private authService: AuthService) {
		this.firstLaunch = !this.backendSetupService.hasSavedAnUrl();
	}

	ngOnInit() {
		this.slices$.next([new Slice()]);
	}

	/**
     * Return `TRUE` if the connection is requested.
	 *
     * There are 2 possible reasons for that :
     * - This is the first connection on WibKac
     * - The user is not connected for this session
     */
	connectionIsNeeded(): boolean {
		return (!this.authService.isConnected() && !this.firstLaunch);
	}

}
