import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuthService } from '../auth/auth.service';

/**
 * This service is handling the fact that the installation on this desktop has been completed.
 */
@Injectable({
	providedIn: 'root'
})
export class InstallService {

	private installCompleteSubject$ = new BehaviorSubject<boolean>(false);

	/**
	 * This `observable` will emit a **true** if the installation has been completed.
	 */
	public installComplete$ = this.installCompleteSubject$.asObservable();

	constructor() {
		this.installCompleteSubject$.next(('1' === localStorage.getItem('installation')));
	}

	/**
	 * Save the fact that the install is complete.
	 */
	public installComplete() {
		localStorage.setItem('installation', '1');
		this.installCompleteSubject$.next(true);
	}

	/**
	 * Simulate tthe fact that the installation is undone.
	 *
	 * This method has been created for testing purpose.
	 */
	public uninstall() {
		localStorage.clear();
		this.installCompleteSubject$.next(false);
	}


}
