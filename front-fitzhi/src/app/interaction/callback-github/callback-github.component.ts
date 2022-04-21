import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GithubToken } from 'src/app/data/github-token';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';

@Component({
	selector: 'app-callback-github',
	templateUrl: './callback-github.component.html',
	styleUrls: ['./callback-github.component.css']
})
export class CallbackGithubComponent implements OnInit, AfterViewInit {

	private code: string;

	constructor(
		private backendSetupService: BackendSetupService,
		private httpClient: HttpClient,
		private route: ActivatedRoute,
		private router: Router) { }
	
	ngOnInit(): void {
		if (this.route.snapshot.queryParams['code']) {
			this.code = this.route.snapshot.queryParams['code'];
			if (traceOn()) {
				console.log ('Loaded code from the Github server : ' + this.code);
			}
		}
	}
	
	ngAfterViewInit(): void {

		const body = { "openIdServer": "GITHUB", "idToken": this.code };

		this.httpClient
			.post<GithubToken>(this.backendSetupService.url() + '/admin/openId/primeRegister', body)
				.subscribe({
					next: token => {
						if (traceOn()) {
							console.log( token);
							console.log (token.access_token);
							console.log (token.token_type);
							console.log (token.scope);
						}
					}
		});
	}
}
