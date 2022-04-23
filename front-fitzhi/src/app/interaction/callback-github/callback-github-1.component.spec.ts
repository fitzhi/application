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


describe('CallbackGithubComponent', () => {
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
	let backendSetupService: BackendSetupService;

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

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		installService = TestBed.inject(InstallService);
		spyOn(installService, 'isComplete').and.returnValue(false);

		skillService = TestBed.inject(SkillService);
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
		const req = httpTestingController.expectOne('URL_OF_SERVER/api/admin/openId/primeRegister');
		expect(req.request.method).toBe('POST');

		const token = new OpenIdToken();
		token.origin = new Origin('token_1234');
		token.origin.scope = 'read, write';
		token.origin.token_type = 'token';

		const staff = new Collaborator();
		staff.idStaff = 1789;
		req.flush(new OpenIdTokenStaff(token, staff));
	}

	it('should be successfully created.', () => {
		expect(component).toBeTruthy();
	});

	it('should call the Fitzhi backend server with the code sent by Github.', () => {

		// We do not need to load the skills.
		spyOn(skillService, 'loadSkills').and.returnValue(null);
		spyOn(installService, 'isVeryFirstInstall').and.returnValue(false);

		const spyOnStaffService = spyOn(staffService, 'changeCollaborator').and.returnValue(null);
		const spyOnTokenService = spyOn(tokenService, 'saveToken').and.returnValue(null);
		const spyOnAuthService = spyOn(authService, 'setConnect').and.returnValue(null);
		const spyOnProjectService = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		const spyOnStaffListService = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		const spyOnMessageService = spyOn(messageService, 'success').and.returnValue(null);
		const navigateSpy = spyOn(router, 'navigateByUrl');

		mockRestCall();

		expect(spyOnStaffService).toHaveBeenCalled();
		expect(spyOnTokenService).toHaveBeenCalled();
		expect(spyOnAuthService).toHaveBeenCalled();
		expect(spyOnProjectService).toHaveBeenCalled();
		expect(spyOnStaffListService).toHaveBeenCalled();
		expect(spyOnMessageService).toHaveBeenCalled();
		expect(navigateSpy).toHaveBeenCalledWith('/user/1789');

	});

	it('should NOT have to save the "installation done" status, when this installation has already be executed.', () => {

		// We do not need to load the skills.
		spyOn(skillService, 'loadSkills').and.returnValue(null);

		// This installation has already be executed.
		// This is not the first installation.
		spyOn(installService, 'isVeryFirstInstall').and.returnValue(false);

		const spy = spyOn(backendSetupService, 'saveVeryFirstConnection$').and.returnValue(null);

		spyOn(staffService, 'changeCollaborator').and.returnValue(null);
		spyOn(tokenService, 'saveToken').and.returnValue(null);
		spyOn(authService, 'setConnect').and.returnValue(null);
		spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		spyOn(messageService, 'success').and.returnValue(null);
		spyOn(router, 'navigateByUrl');

		mockRestCall();

		expect(spy).not.toHaveBeenCalled();

	});

	it('should save the "installation done" status, when the very first installation is executed.', () => {

		// We do not need to load the skills.
		spyOn(skillService, 'loadSkills').and.returnValue(null);

		// This is the "use case" of the first install.
		spyOn(installService, 'isVeryFirstInstall').and.returnValue(true);

		const spy = spyOn(backendSetupService, 'saveVeryFirstConnection$').and.returnValue(null);

		spyOn(staffService, 'changeCollaborator').and.returnValue(null);
		spyOn(tokenService, 'saveToken').and.returnValue(null);
		spyOn(authService, 'setConnect').and.returnValue(null);
		spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);
		spyOn(messageService, 'success').and.returnValue(null);
		spyOn(router, 'navigateByUrl');

		mockRestCall();

		expect(spy).toHaveBeenCalled();
	});

});
