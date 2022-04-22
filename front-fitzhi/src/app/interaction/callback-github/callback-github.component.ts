import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/admin/service/auth/auth.service';
import { Token } from 'src/app/admin/service/token/token';
import { TokenService } from 'src/app/admin/service/token/token.service';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { traceOn } from 'src/app/global';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
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
		private backendSetupService: BackendSetupService,
		private tokenService: TokenService,
		private authService: AuthService,
		private staffService: StaffService,
		private projectService: ProjectService,
		private staffListService: StaffListService,
		private messageService: MessageService,
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

		this.httpClient.post<OpenIdTokenStaff>(this.backendSetupService.url() + '/admin/openId/primeRegister', body)
			.subscribe({
				next: oits => {
					const staff = oits.staff;
					if (traceOn()) {
						console.log (`${staff.idStaff} ${staff.firstName} ${staff.lastName} has been created in Fitzi from its Github token`);
					}
					this.staffService.changeCollaborator(staff);
					const token = new Token();
					// We use the JWT as access token for this authenticated user.
					token.access_token = oits.openIdToken.origin.access_token;
					this.tokenService.saveToken(token);

					// This registration through the mechanism of openid tokens, automatically connects the user.
					this.authService.setConnect();

					// We load the projects and start the refresh process.
					this.projectService.startLoadingProjects();
					// We load the staff and start the refresh process.
					this.staffListService.startLoadingStaff();

					this.messageService.success(`{staff.firtName} {staff.lastName} is successfully created.`);

//					this.router.navigateByUrl('/user/' + staff.idStaff);
				}
		});
	}
}
