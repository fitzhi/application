import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { GoogleService } from 'src/app/service/google/google.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { BaseDirective } from '../../base/base-directive.directive';
import { InstallService } from '../service/install/install.service';
import { Token } from '../service/token/token';
import { TokenService } from '../service/token/token.service';
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
	@Output() messengerUserRegistered$ = new EventEmitter<number>();

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
	public localOnly$ = new BehaviorSubject<boolean>(true);

	constructor(
		private installService: InstallService,
		private referentialService: ReferentialService,
		private staffService: StaffService,
		private tokenService: TokenService,
		private googleService: GoogleService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							this.localOnly$.next((this.referentialService.openidServers.length === 0));
						}
					}
				}
			)
		);

		this.subscriptions.add(
			this.googleService.isRegistered$
				.pipe(switchMap( doneAndOk => (doneAndOk) ? this.googleService.isAuthenticated$ : EMPTY))
				.subscribe({
					next: authenticated => {
						if (authenticated) {
							if (traceOn()) {
								console.log ('%s is logged in', this.googleService.googleToken.name);
							}
							this.staffService.openIdRegisterUser$(this.veryFirstConnection, "GOOGLE", this.googleService.jwt).subscribe({
								next: staff => {
									if (traceOn()) {
										console.log ('%s has been created in Fitzi from its Google token', staff.lastName)
									}
									this.staffService.changeCollaborator(staff);
									const token = new Token();
									// We use the JWT as access token for this authenticated user.
									token.access_token = this.googleService.googleToken.sub;
									this.tokenService.saveToken(token);
									this.messengerUserRegistered$.emit(staff.idStaff);			
								}
							})
						}
					}
				})
		);
	}

	/**
	 * Skip the registration of a new user.
	 */
	public skip() {
		// We do know at this point the staff identifier corresponding to this user.
		this.installService.installComplete();
		this.messengerSkipAndConnect.emit(true);
	}


	public onRegisterUser(idStaff: number) {
		if (traceOn()) {
			console.log('onRegisterUser(%d)', idStaff);
		}
		this.messengerUserRegistered$.emit(idStaff);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
