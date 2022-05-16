import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { GithubService } from 'src/app/service/github/github.service';
import { GoogleService } from 'src/app/service/google/google.service';

@Component({
	selector: 'app-alternative-openid-connection',
	templateUrl: './alternative-openid-connection.component.html',
	styleUrls: ['./alternative-openid-connection.component.css']
})
export class AlternativeOpenidConnectionComponent extends BaseDirective implements OnDestroy, OnInit {

	public google = true;

	constructor(
		public googleService: GoogleService,
		public githubService: GithubService) { super(); }

	ngOnInit(): void {
		this.subscriptions.add(
			this.googleService.isRegistered$.subscribe({
				next: isRegistered => (isRegistered) ? this.googleService.initialize(document) : null
			})
		);
	}

	/**
	 * All subscriptions are closed in the BaseDirective.
	 */
	public ngOnDestroy() {
		super.ngOnDestroy();
	}

}
