import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BaseDirective } from 'src/app/base/base-directive.directive';
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

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$
				.subscribe({
					next: doneAndOk => {
						if (doneAndOk) {
							console.log (this.referentialService.openidServers.length);
							this.localOnlyOauthSubject$.next((this.referentialService.openidServers.length === 0));
						}
					}
				}
			)
		);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	 ngOnDestroy() {
		super.ngOnDestroy();
	}

}
