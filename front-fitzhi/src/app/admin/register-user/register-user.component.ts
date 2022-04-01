import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthenticationServer } from 'src/app/data/authentication-server';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { BaseDirective } from '../../base/base-directive.directive';
import { InstallService } from '../service/install/install.service';
import { RegisterUserFormComponent } from './register-user-form/register-user-form.component';

@Component({
	selector: 'app-register-user',
	templateUrl: './register-user.component.html',
	styleUrls: ['./register-user.component.css']
})
export class RegisterUserComponent extends BaseDirective implements OnInit, OnDestroy {

	@ViewChild(RegisterUserFormComponent) registerUserFormComponent: RegisterUserFormComponent;

	/**
	 * Is this ever the first connection to this server, assuming that the user has to be "administrator" ?
	 */
	 @Input() veryFirstConnection: boolean;

	 /**
	 * We'll send to the parent component (startingSetup) the new user has been created.
	 */
	@Output() messengerSkipAndConnect = new EventEmitter<boolean>();

	/**
	 * This variable monitor the UI between 2 possible cases :
	 * 
	 * - ONE UNIQUE local authentication server, the fitzhi server
	 * - Multiple authentication servers as the Fitzhi backend and the Google server or instance
	 */
	public localOnly = true;

	constructor(
		private installService: InstallService,
		private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.referentialService.referentialLoaded$
			.pipe(switchMap(doneAndOk => (doneAndOk) ? this.referentialService.authenticationServers$ : EMPTY))
			.subscribe({
				next: (servers: AuthenticationServer[]) => {
					this.localOnly = (servers.length === 0);
				}
			})
	}

	/**
	 * Skip the registration of a new user.
	 */
	public skip() {
		// We do know at this point the staff identifier corresponding to this user.
		this.installService.installComplete();
		this.messengerSkipAndConnect.emit(true);
	}


	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
