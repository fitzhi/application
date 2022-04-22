import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/admin/service/auth/auth.service';
import { Token } from 'src/app/admin/service/token/token';
import { TokenService } from 'src/app/admin/service/token/token.service';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { GithubService } from 'src/app/service/github/github.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { MessageService } from '../message/message.service';

@Component({
	selector: 'app-callback-github',
	templateUrl: './callback-github.component.html',
	styleUrls: ['./callback-github.component.css']
})
export class CallbackGithubComponent implements OnInit, AfterViewInit {

	private code: string;

	constructor(
		private githubService: GithubService,
		private projectService: ProjectService,
		private httpClient: HttpClient,
		private authService: AuthService,
		private staffService: StaffService,
		private staffListService: StaffListService,
		private backendSetupService: BackendSetupService,
		private messageService: MessageService,
		private tokenService: TokenService,
		private router: Router,
		private route: ActivatedRoute) { }
	
	ngOnInit(): void {
		if (this.route.snapshot.queryParams['code']) {
			this.code = this.route.snapshot.queryParams['code'];
			if (traceOn()) {
				console.log ('Loaded code from the Github server : ' + this.code);
			}
		}
	}
	
	ngAfterViewInit(): void {
		this.primeRegister(this.code);
	}

	/**
	 * Register the Github user associated with the given code
	 * @param code the returned code
	 */
	 primeRegister(code: string) {

		const body = { "openIdServer": this.githubService.GITHUB_SERVER_ID, "idToken": code };

		this.httpClient.post<OpenIdTokenStaff>(this.backendSetupService.url() + '/admin/openId/primeRegister', body)
			.subscribe({
				next: oits => {
					const staff = oits.staff;
					if (traceOn()) {
						console.log (`${staff.idStaff} ${staff.firstName} ${staff.lastName} has been created in Fitzi from its Github token`);
					}
					this.staffService.changeCollaborator(staff);
					const token = new Token();
					// We use the GITHUB token as access token for this authenticated user.
					token.access_token = oits.openIdToken.origin.access_token;
					if (traceOn()) {
						console.log ('Storing the token %s', token.access_token);
					}
					this.tokenService.saveToken(token);

					// This registration through the mechanism of openid tokens, automatically connects the user.
					this.authService.setConnect();

					// We load the projects and start the refresh process.
					this.projectService.startLoadingProjects();
					// We load the staff and start the refresh process.
					this.staffListService.startLoadingStaff();

					this.messageService.success(`${staff.firstName} ${staff.lastName} is successfully created.`);

					this.router.navigateByUrl(`/user/${staff.idStaff}`);
				}
		});
	}

}
