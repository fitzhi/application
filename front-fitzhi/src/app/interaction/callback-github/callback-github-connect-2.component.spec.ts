import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from 'src/app/admin/service/auth/auth.service';
import { InstallService } from 'src/app/admin/service/install/install.service';
import { TokenService } from 'src/app/admin/service/token/token.service';
import { Collaborator } from 'src/app/data/collaborator';
import { OpenIdToken, Origin } from 'src/app/data/OpenIdToken';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { MessageService } from '../message/message.service';
import { CallbackGithubComponent } from './callback-github.component';


describe('CallbackGithubComponent (when connecting a user)', () => {
	let component: CallbackGithubComponent;
	let fixture: ComponentFixture<CallbackGithubComponent>;
	let httpTestingController: HttpTestingController;
	let installService: InstallService;
	let skillService: SkillService;
	let staffService: StaffService;
	let tokenService: TokenService;
	let authService: AuthService;
	let projectService: ProjectService;
	let staffListService: StaffListService;
	let messageService: MessageService;
	let router: Router;

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

		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		skillService = TestBed.inject(SkillService);
		
		installService = TestBed.inject(InstallService);
		installService.setVeryFirstConnection(false); 
		installService.installComplete();

		staffService = TestBed.inject(StaffService);
		tokenService = TestBed.inject(TokenService);
		authService = TestBed.inject(AuthService);
		projectService = TestBed.inject(ProjectService);
		staffListService = TestBed.inject(StaffListService);
		messageService = TestBed.inject(MessageService);
		router = TestBed.inject(Router);

		fixture.detectChanges();
	});

	function mockRestCall() {
		
		const req1 = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/connect');
		expect(req1.request.method).toBe('POST');

		const token = new OpenIdToken();
		token.origin = new Origin('token_12345');
		token.origin.scope = 'read, write';
		token.origin.token_type = 'token';

		const staff = new Collaborator();
		staff.idStaff = 1789;
		req1.flush(new OpenIdTokenStaff(token, staff));

	}

	function mockRestCallRegisterInError() {
		const req1 = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/connect');
		expect(req1.request.method).toBe('POST');
		req1.error(new ErrorEvent('error'), { status: 500, statusText: 'Error message!' });
	}

	it('should sign into Fitzhi the selected Github user.', () => {

		const spyOnTokenService = spyOn(tokenService, 'saveToken').and.returnValue(null);
		const spyOnAuthService = spyOn(authService, 'setConnect').and.returnValue(null);
		const spyOnProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		const spyOnStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyOnMessageService = spyOn(messageService, 'success').and.returnValue(null);
		const navigateSpy = spyOn(router, 'navigateByUrl');

		// We do not need to load the skills.
		spyOn (skillService, 'loadSkills').and.returnValue(null);

		mockRestCall();

		expect(spyOnTokenService).toHaveBeenCalled();
		expect(spyOnAuthService).toHaveBeenCalled();
		expect(spyOnProjectService).toHaveBeenCalled();
		expect(spyOnStaffListService).toHaveBeenCalled();
		expect(spyOnMessageService).toHaveBeenCalled();
		expect(navigateSpy).toHaveBeenCalledWith('/welcome');

	});

	it('should handle a connection error and redirect the user to the login page.', () => {

		const spyOnTokenService = spyOn(tokenService, 'saveToken').and.returnValue(null);
		const spyOnAuthService = spyOn(authService, 'setConnect').and.returnValue(null);
		const spyOnProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		const spyOnStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyOnMessageService = spyOn(messageService, 'error').and.returnValue(null);
		const navigateSpy = spyOn(router, 'navigateByUrl');

		// We do not need to load the skills.
		spyOn (skillService, 'loadSkills').and.returnValue(null);

		mockRestCallRegisterInError();

		expect(spyOnTokenService).not.toHaveBeenCalled();
		expect(spyOnAuthService).not.toHaveBeenCalled();
		expect(spyOnProjectService).not.toHaveBeenCalled();
		expect(spyOnStaffListService).not.toHaveBeenCalled();
		expect(spyOnMessageService).not.toHaveBeenCalled();
		expect(navigateSpy).not.toHaveBeenCalledWith('/welcome');

	});

});
