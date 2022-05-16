import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { Collaborator } from 'src/app/data/collaborator';
import { GoogleToken } from 'src/app/data/google-token';
import { OpenIdToken } from 'src/app/data/OpenIdToken';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { AlternativeOpenidConnectionComponent } from 'src/app/interaction/alternative-openid-connection/alternative-openid-connection.component';
import { CinematicService } from 'src/app/service/cinematic.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { AuthService } from '../service/auth/auth.service';
import { InstallService } from '../service/install/install.service';
import { RegisterUserFormComponent } from './register-user-form/register-user-form.component';
import { RegisterUserComponent } from './register-user.component';


describe('registerUserComponent in the openID authentication scenario', () => {
	let component: RegisterUserComponent;
	let fixture: ComponentFixture<RegisterUserComponent>;

	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;
	let googleService: GoogleService;
	let staffService: StaffService;
	let authService: AuthService;
	let projectService: ProjectService;
	let staffListService: StaffListService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [RegisterUserComponent, RegisterUserFormComponent, AlternativeOpenidConnectionComponent],
			providers: [ReferentialService, CinematicService, InstallService, GoogleService, StaffService, ProjectService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		// Inject the http service and test controller for each test
		httpClient = TestBed.inject(HttpClient);
		httpTestingController = TestBed.inject(HttpTestingController);

		fixture = TestBed.createComponent(RegisterUserComponent);
		component = fixture.componentInstance;

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.openidServers.push( { 'serverId': 'GOOGLE', 'clientId': 'myClientId'} );
		referentialService.referentialLoaded$.next(true);

		googleService = TestBed.inject(GoogleService);
		staffService = TestBed.inject(StaffService);
		authService = TestBed.inject(AuthService);
		projectService = TestBed.inject(ProjectService);
		staffListService = TestBed.inject(StaffListService);

		fixture.detectChanges();
	});

	it('should display the Google connection if the openId google authentication server has been declared.', () => {
		const localOauthOnly = fixture.debugElement.nativeElement.querySelector('#localOauthOnly');
		expect(localOauthOnly).toBeNull();
		const multiOauth = fixture.debugElement.nativeElement.querySelector('#multipleOauth');
		expect(multiOauth).not.toBeNull();
	});

	it('should handle correctly the registration of new user base on his OpenID JWT.', () => {

		const spyInitialize = spyOn(googleService, 'initialize').and.returnValue(null);
		const googleToken = new GoogleToken();
		googleToken.name = 'Frédéric VIDAL';
		googleService.googleToken = googleToken;

		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.firstName = 'Frédéric';
		staff.lastName = 'Vidal';
		const spyStaffService = spyOn(staffService, 'openIdRegisterUser$').and.returnValue(of(
			new OpenIdTokenStaff(new OpenIdToken(), staff)));

		const spyStaffService2 = spyOn(staffService, 'changeCollaborator');
		const spyAuthService = spyOn(authService, 'setConnect');
		const spyProjectService = spyOn(projectService, 'startLoadingProjects');
		const spyStaffListService = spyOn(staffListService, 'startLoadingStaff');

		spyOn(component.messengerUserRegistered$, 'emit');

		googleService.register();
		googleService.signIn();

		fixture.detectChanges();

		expect(spyStaffService).toHaveBeenCalled();
		expect(spyStaffService2).toHaveBeenCalledWith(staff);
		expect(spyAuthService).toHaveBeenCalled();
		expect(spyProjectService).toHaveBeenCalled();
		expect(spyStaffListService).toHaveBeenCalled();

		expect(component.messengerUserRegistered$.emit).toHaveBeenCalled();

	});

});
