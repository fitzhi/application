import { Component, EventEmitter, Input, NgZone, OnDestroy, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { OpenIdCredentials } from 'src/app/data/open-id-credentials';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { AuthService } from '../service/auth/auth.service';
import { InstallService } from '../service/install/install.service';
import { Token } from '../service/token/token';
import { TokenService } from '../service/token/token.service';

@Component({
	selector: 'app-connect-user',
	templateUrl: './connect-user.component.html',
	styleUrls: ['./connect-user.component.css']
})
export class ConnectUserComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * Are we entering in this component, just by routing directly into '/login'
	 */
	@Input() directLogin = false;

	/**
	 * We'll send to the parent component (startingSetup) the new user is connected.
	 */
	@Output() messengerUserConnected$ = new EventEmitter<boolean>();

	private localOnlyOauthSubject$ = new BehaviorSubject<boolean>(true);

	public localOnlyOauth$ = this.localOnlyOauthSubject$.asObservable();

	constructor(
		private referentialService: ReferentialService,
		private authService: AuthService,
		private messageService: MessageService,
		private installService: InstallService,
		private router: Router,
		private ngZone: NgZone,
		private tokenService: TokenService,
		private projectService: ProjectService,
		private staffListService: StaffListService,
		private googleService: GoogleService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.installService.installComplete$
				.pipe(switchMap(doneAndOk => (doneAndOk ? this.referentialService.referentialLoaded$ : EMPTY)))
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							if (traceOn()) {
								console.log ('%d declared openID server(s)', this.referentialService.openidServers.length);
							}
							this.localOnlyOauthSubject$.next(
								(this.referentialService.openidServers.length === 0)
							);
						}
					}
				})
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
							this.authService.connectOpenId$(new OpenIdCredentials('GOOGLE', this.googleService.jwt))
								.subscribe({
									next: staff => {

										this.messageService.success(`${staff.firstName} ${staff.lastName} is successfully connected`);
										const token = new Token();
										// We use the JWT as access token for this authenticated user.
										token.access_token = this.googleService.googleToken.sub;
										this.tokenService.saveToken(token);
									
										this.authService.setConnect();
										
										// We load the projects and start the refresh process.
										this.projectService.startLoadingProjects();
										// We load the staff and start the refresh process.
										this.staffListService.startLoadingStaff();

										this.ngZone.run(() => { this.router.navigate(['/welcome'], {}) });
									},
									error: error => this.messageService.error(error.statusText)
								})

						}
					}
				})
		);
	}

	/**
	 * We transfert the connection status to the parent.
	 * @param connected boolean corresponding to the connection status
	 */
	public onUserConnected($event) {
		if (traceOn()) {
			console.log ('Tranfering the connection status %d', $event);
		}
		this.messengerUserConnected$.emit($event);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
