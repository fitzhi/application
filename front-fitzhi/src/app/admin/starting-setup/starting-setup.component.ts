import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { InstallService } from '../service/install/install.service';

@Component({
	selector: 'app-starting-setup',
	templateUrl: './starting-setup.component.html',
	styleUrls: ['./starting-setup.component.css']
})
export class StartingSetupComponent extends BaseDirective implements OnDestroy {

	/**
	 * The main stepper is passed in order to procede a programmatly step.next().
	 */
	@ViewChild('stepper', { static: true }) stepper: MatStepper;

	/**
	 * This status will be setup to TRUE, FALSE otherwise.
	 */
	public veryFirstConnection = true;

	/**
	 * Are we in the very first connection ?
	 */
	private _veryFirstConnection$ = new Subject<boolean>();

	/**
	 * Are we in the very first connection ?
	 */
	public veryFirstConnection$ = this._veryFirstConnection$.asObservable();

	/**
	 * Array representing the fact that each step has been completed.
	 */
	completed: Array<boolean> = [false, false, false, false];

	/**
	 * Current staff identifier.
	 */
	idStaff: number;

	/**
	 * Current staff.
	 */
	staff: Collaborator;

	/**
	 * Label of the second step.
	 */
	labelUser = 'User';


	constructor(
		private backendSetupService: BackendSetupService,
		private referentialService: ReferentialService,
		private skillService: SkillService,
		private installService: InstallService,
		private router: Router,
		private httpClient: HttpClient) { super(); }

	/**
	 * Setup the fact that this is the very first connection.
	 */
	onChangeVeryFirstConnection($event: boolean) {
		if (traceOn()) {
			console.log('veryFirstConnection :', $event);
		}

		this.veryFirstConnection = $event;
		this._veryFirstConnection$.next(this.veryFirstConnection);

		this.labelUser = ($event) ? 'First admin user' : 'First registration';

		this.completed[0] = true;
		this.referentialService.loadAllReferentials();
		this.skillService.loadSkills();

		setTimeout(() => this.stepper.next(), 0);

	}

	/**
	 * Catch the staff identifier created the registerUserComponent.
	 */
	setRegisteredUser($event: number) {

		if (traceOn()) {
			console.log('idStaff created :', $event);
		}
		// Operation has been cancelled.
		if ($event === -1) {
			this.completed[1] = false;
			setTimeout(() => {
				this.stepper.previous();
			}, 0);
			return;
		}
		this.completed[1] = true;
		this.idStaff = $event;

		setTimeout(() => this.stepper.next(), 0);
	}

	/**
	 * Skip the setup process and proceed the connection.
	 */
	skipAndConnect($event: number) {
		if ( traceOn()) {
			console.log ('Skip and connect (' + $event + ')');
		}
		if ($event) {
			this.router.navigate(['/login'], {});
		}
	}

	/**
	 * Catch the fact that the user is successfully connected, OR NOT...
	 */
	setConnection($event: boolean) {
		if ($event) {
			this.completed[2] = true;
			setTimeout(() => {
				this.stepper.next();
			}, 0);
		} else {
			this.completed[2] = false;
			this.completed[3] = false;
			if (traceOn()) {
				console.log('Connection failed. Invalid User/Password');
			}
		}
	}

	/**
	 * Catch the complete staff entity, updated by the staff-form.
	 * @param $event the staff member created
	 */
	setStaffUpdatedForUser($event: Collaborator) {
		if (traceOn()) {
			console.log('staff updated for :', $event.lastName);
		}

		if (!this.veryFirstConnection) {
			if (traceOn()) {
				console.log('We are not in mode "very first connection".');
			}
			this.nextStepAfterStaffUpdate($event);
		} else {
			this.subscriptions.add(
				this.httpClient.post<Boolean>(this.backendSetupService.url() + '/admin/saveVeryFirstConnection', '')
					.subscribe({
						next:  veryFirstConnectionIsRegistered => {
							if (traceOn() && veryFirstConnectionIsRegistered) {
								console.log('The very first connection is registered into Fitzhi.');
							}
							this.nextStepAfterStaffUpdate($event);
						}
					}
				)
			);
		}
	}

	private nextStepAfterStaffUpdate(staff: Collaborator): void {
		this.completed[3] = true;
		this.installService.installComplete();
		this.staff = staff;
		setTimeout(() => this.stepper.next(), 0);
	}

	/**
	 * @param $event we move from one step in the installation.
	 */
	selectionChange($event) {
		window.scroll(0, 0);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
