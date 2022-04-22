import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { time } from 'console';
import { AuthService } from 'src/app/admin/service/auth/auth.service';
import { TokenService } from 'src/app/admin/service/token/token.service';
import { Collaborator } from 'src/app/data/collaborator';
import { GithubToken } from 'src/app/data/github-token';
import { OpenIdToken, Origin } from 'src/app/data/OpenIdToken';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { MessageService } from '../message/message.service';
import { CallbackGithubComponent } from './callback-github.component';


describe('CallbackGithubComponent', () => {
	let component: CallbackGithubComponent;
	let fixture: ComponentFixture<CallbackGithubComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ CallbackGithubComponent ],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						snapshot: {queryParams: {code: 'thecode'}}
					}
				},
				CinematicService,
			],
			imports: [
				MatDialogModule,
				HttpClientTestingModule,
				RouterTestingModule.withRoutes([])
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CallbackGithubComponent);
		component = fixture.componentInstance;
		httpTestingController = TestBed.inject(HttpTestingController);

		fixture.detectChanges();
	});

	it('should be successfully created.', () => {
		expect(component).toBeTruthy();
	});

	it('should call the Fitzhi backend server with the code sent by Github!', () => {

		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');
		fixture.detectChanges();

		const staffService = TestBed.inject(StaffService);
		const spyOnStaffService = spyOn(staffService,'changeCollaborator').and.returnValue(null);

		const tokenService = TestBed.inject(TokenService);
		const spyOnTokenService = spyOn(tokenService,'saveToken').and.returnValue(null);

		const authService = TestBed.inject(AuthService);
		const spyOnAuthService = spyOn(authService,'setConnect').and.returnValue(null);

		const projectService = TestBed.inject(ProjectService);
		const spyOnProjectService = spyOn(projectService,'startLoadingProjects').and.returnValue(null);

		const staffListService = TestBed.inject(StaffListService);
		const spyOnStaffListService = spyOn(staffListService,'startLoadingStaff').and.returnValue(null);

		const messageService = TestBed.inject(MessageService);
		const spyOnMessageService = spyOn(messageService, 'success').and.returnValue(null);

		const router = TestBed.inject(Router);
		const navigateSpy = spyOn(router, 'navigateByUrl');

		const req = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/primeRegister');
		expect(req.request.method).toBe('POST');

		const token = new OpenIdToken();
		token.origin = new Origin('token_1234');
		token.origin.scope = 'read, write';
		token.origin.token_type = 'token'

		const staff = new Collaborator();
		staff.idStaff = 1789;
		req.flush(new OpenIdTokenStaff(token, staff));

		expect(spyOnStaffService).toHaveBeenCalled();
		expect(spyOnTokenService).toHaveBeenCalled();
		expect(spyOnAuthService).toHaveBeenCalled();
		expect(spyOnProjectService).toHaveBeenCalled();
		expect(spyOnStaffListService).toHaveBeenCalled();
		expect(spyOnMessageService).toHaveBeenCalled();
		expect(navigateSpy).toHaveBeenCalledWith('/user/1789');

	});

});
