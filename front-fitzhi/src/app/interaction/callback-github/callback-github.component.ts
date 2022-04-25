import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/admin/service/auth/auth.service';
import { InstallService } from 'src/app/admin/service/install/install.service';
import { Token } from 'src/app/admin/service/token/token';
import { TokenService } from 'src/app/admin/service/token/token.service';
import { Collaborator } from 'src/app/data/collaborator';
import { OpenIdCredentials } from 'src/app/data/open-id-credentials';
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
		private installService: InstallService,
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
		// The use case is
		// either the registration of a new user in the application.
		if (!this.installService.isComplete()) {
			this.register(this.code);
		} else {
			// or the connection of a registered user.
			this.connect(this.code)
		}
	}
	
	/**
	 * Register the Github user associated with the given code
	 * @param code the returned code
	 */
	 connect(code: string) {
		 if (traceOn()) {
			 console.log ('Connecting github user with code %s', code);
		 }
		 const oic = new OpenIdCredentials(this.githubService.GITHUB_SERVER_ID, code);
		 this.authService.connectOpenId$(oic).subscribe({
			next: (oits: OpenIdTokenStaff) => {
				const staff = oits.staff;
				if (traceOn()) {
					console.log ("%s %s %s is connected.", staff.idStaff, staff.firstName, staff.lastName)
				}
				this.messageService.success(`${staff.firstName} ${staff.lastName} is successfully connected`);

				const token = new Token();
				// We use the JWT as access token for this authenticated user.
				token.access_token = oits.openIdToken.origin.access_token;
				this.tokenService.saveToken(token);

				this.completeConnection();

				this.router.navigateByUrl('/welcome');
			}
		 });
	 }

	/**
	 * Register the Github user associated with the given code
	 * @param code the returned code
	 */
	register(code: string) {

		const body = { 'openIdServer': this.githubService.GITHUB_SERVER_ID, 'idToken': code };

		const url = this.backendSetupService.url() + '/admin/openId/' +
			((this.installService.isVeryFirstInstall())  ? 'primeRegister' : 'register');
		if (traceOn()) {
			console.log ('Accessing %s to register this user', url);
		}

		this.httpClient.post<OpenIdTokenStaff>(url, body)
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

					this.completeConnection();

					this.messageService.success(`${staff.firstName} ${staff.lastName} is successfully created.`);

					this.installService.installComplete();
					if (this.installService.isVeryFirstInstall()) {
						this.backendSetupService.saveVeryFirstConnection$().subscribe({
							next: doneAndOk => {
								if (doneAndOk) {
									console.log ('Registration done');
								}
								this.router.navigateByUrl(`/user/${staff.idStaff}`);
							},
							error: error => console.log (error)
						});
					} else {
						this.router.navigateByUrl(`/user/${staff.idStaff}`);
					}
				},
				error: error => {
					if (traceOn()) {
						console.log (error.message)
					}
					this.installService.uninstall();
					this.router.navigateByUrl('/');
				}
		});
	}

	/**
	 * Complete the connection by doing some initialization.
	 */
	private completeConnection() {
		// This registration through the mechanism of openid tokens, automatically connects the user.
		this.authService.setConnect();

		// We load the projects and start the refresh process.
		this.projectService.startLoadingProjects();
		// We load the staff and start the refresh process.
		this.staffListService.startLoadingStaff();

	}

}
