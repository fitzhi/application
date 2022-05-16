import { Component, EventEmitter, Input, NgZone, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { LoginEvent } from 'src/app/data/login-event';
import { LoginMode } from 'src/app/data/login-mode';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { environment } from 'src/environments/environment';
import { BaseDirective } from '../../base/base-directive.directive';
import { AuthService } from '../service/auth/auth.service';
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
	@Output() messengerUserRegistered$ = new EventEmitter<LoginEvent>();

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
		private authService: AuthService,
		private googleService: GoogleService,
		private projectService: ProjectService,
		private messageService: MessageService,
		private ngZone: NgZone,
		private staffListService: StaffListService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							if (traceOn()) {
								console.log ('Show or Hide the %d openID panel.', this.referentialService.openidServers.length);
							}
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
							this.staffService.openIdRegisterUser$(this.veryFirstConnection, 'GOOGLE', this.googleService.jwt).subscribe({
								next: (openIdStaff) => {
									const staff = openIdStaff.staff;
									if (traceOn()) {
										console.log ('%s has been created in Fitzi from its Google token', staff.lastName);
									}
									this.staffService.changeCollaborator(staff);
									const token = new Token();
									// We use the JWT as access token for this authenticated user.
									token.access_token = this.googleService.googleToken.sub;
									this.tokenService.saveToken(token);

									// This registration through the mechanism of openid tokens, automatically connects the user.
									this.authService.setConnect();

									// We load the projects and start the refresh process.
									this.projectService.startLoadingProjects();
									// We load the staff and start the refresh process.
									this.staffListService.startLoadingStaff();

									this.messageService.success(`{staff.firtName} {staff.lastName} is successfully created.`);

									this.messengerUserRegistered$.emit(new LoginEvent(staff.idStaff, LoginMode.OPENID));
								},
								error: response => {
									if (traceOn()) {
										console.log ('error', response.error.message);
									}
									setTimeout(() => {
										this.ngZone.run(() => this.messageService.error(response.error.message) );
									}, 0);
								}
							});
						}
					},
				})
		);
	}

	/**
	 * Skip the registration of a new user.
	 */
	public skip() {
		this.installService.installComplete();
		// We do know at this point the staff identifier corresponding to this user.
		// We reload the application in production mode the production mode.
		if (!environment.test) {
			window.location.reload();
		}
	}


	public onRegisterUser($event: LoginEvent) {
		if (traceOn()) {
			console.log('onRegisterUser(%d)', $event.idStaff);
		}
		this.messengerUserRegistered$.emit($event);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
