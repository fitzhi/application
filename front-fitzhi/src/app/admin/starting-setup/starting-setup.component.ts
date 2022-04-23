import { HttpClient } from '@angular/common/http';
import { Component, NgZone, OnDestroy, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Collaborator } from 'src/app/data/collaborator';
import { LoginEvent } from 'src/app/data/login-event';
import { LoginMode } from 'src/app/data/login-mode';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { RegisterUserComponent } from '../register-user/register-user.component';
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
	 * The register form.
	 */
	@ViewChild(RegisterUserComponent) register;

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
		public installService: InstallService,
		private router: Router,
		private httpClient: HttpClient,
		private ngZone: NgZone) { super(); }

	/**
	 * Setup the fact that this is the very first connection.
	 */
	onChangeVeryFirstConnection($event: boolean) {

		if (traceOn()) {
			console.log('veryFirstConnection :', $event);
		}

		this.installService.setVeryFirstConnection($event);

		this.labelUser = ($event) ? 'First admin user' : 'First registration';

		this.completed[0] = true;
		this.referentialService.loadAllReferentials();
		this.skillService.loadSkills();

		setTimeout(() => this.stepper.next(), 0);

	}

	/**
	 * Catch the staff identifier created the registerUserComponent.
	 */
	setRegisteredUser($event: LoginEvent) {

		if (traceOn()) {
			console.log(`idStaff created : ${$event.idStaff}`);
		}

		// Operation has been cancelled. We skip backward.
		if ($event.idStaff === -1) {
			this.completed[1] = false;
			setTimeout(() => { this.stepper.previous(); }, 0);
			return;
		}

		this.idStaff = $event.idStaff;
		this.ngZone.run(() => {
			this.completed[1] = true;
			// If we are in openID mode we can skip the connection tab. We are already connected after the registration.
			if ($event.loginMode === LoginMode.OPENID) {
				this.completed[2] = true;
			}
			setTimeout(() => {
				this.stepper.next();
				// Same reason as before.
				if ($event.loginMode === LoginMode.OPENID) { this.stepper.next(); }
			}, 0);
		});
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
			setTimeout(() => this.stepper.next(), 0);
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

		if (!this.installService.isComplete()) {
			if (traceOn()) {
				console.log('We have already installed Fitzhi. This is not the mode "very first connection".');
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

	/**
	 * @param staff the staff saved
	 */
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
