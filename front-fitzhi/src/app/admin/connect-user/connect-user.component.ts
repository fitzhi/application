import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { SlowBuffer } from 'buffer';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { traceOn } from 'src/app/global';
import { GoogleService } from 'src/app/service/google/google.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';

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
		private googleService: GoogleService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							if (traceOn()) {
								console.log ('%d declared openID server(s)', this.referentialService.openidServers.length);
							}
							this.localOnlyOauthSubject$.next((this.referentialService.openidServers.length === 0));
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
