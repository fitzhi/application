import { Component, Input, OnDestroy, OnInit } from '@angular/core';
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
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	 ngOnDestroy() {
		super.ngOnDestroy();
	}

}
