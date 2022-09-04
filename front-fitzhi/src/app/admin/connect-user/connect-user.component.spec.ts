import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Collaborator } from 'src/app/data/collaborator';
import { GoogleToken } from 'src/app/data/google-token';
import { OpenidServer } from 'src/app/data/openid-server';
import { OpenIdTokenStaff } from 'src/app/data/openidtoken-staff';
import { AlternativeOpenidConnectionComponent } from 'src/app/interaction/alternative-openid-connection/alternative-openid-connection.component';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffListComponent } from 'src/app/tabs-staff-list/staff-list/staff-list.component';
import { StaffComponent } from 'src/app/tabs-staff/staff.component';
import { WelcomeComponent } from 'src/app/welcome/welcome.component';
import { CiaoComponent } from '../ciao/ciao.component';
import { AuthService } from '../service/auth/auth.service';
import { InstallService } from '../service/install/install.service';
import { TokenService } from '../service/token/token.service';
import { ConnectUserFormComponent } from './connect-user-form/connect-user-form.component';
import { ConnectUserComponent } from './connect-user.component';


describe('ConnectUserComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let referentialService: ReferentialService;
	let googleService: GoogleService;
	let authService: AuthService;
	let tokenService: TokenService;
	let projectService: ProjectService;
	let staffListService: StaffListService;
	let installService: InstallService;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="background-color: whiteSmoke; margin-top: 30px; margin-left: 100px; padding: 20px">
				<app-connect-user></app-connect-user>
			</div>`})
	class TestHostComponent {
		constructor() {}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ ConnectUserFormComponent, ConnectUserComponent, TestHostComponent, AlternativeOpenidConnectionComponent, CiaoComponent ],
			providers: [ProjectService, CinematicService, FileService, MessageBoxService, FormBuilder, ReferentialService,
					GoogleService, AuthService, ProjectService, StaffListService],
			imports: [HttpClientTestingModule, MatDialogModule, RouterTestingModule, FormsModule, ReactiveFormsModule,
				RouterTestingModule.withRoutes([
					{ path: 'welcome', component: WelcomeComponent },
					{ path: 'ciao', component: CiaoComponent }
				])]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		referentialService = TestBed.inject(ReferentialService);
		googleService = TestBed.inject(GoogleService);
		authService = TestBed.inject(AuthService);
		tokenService = TestBed.inject(TokenService);
		projectService = TestBed.inject(ProjectService);
		staffListService = TestBed.inject(StaffListService);
		installService = TestBed.inject(InstallService);

		installService.installComplete();

		fixture.detectChanges();
	});

	it('should be created without error.', () => {
		expect(component).toBeTruthy();
	});

	it('by default should only show the single connection form.', () => {
		referentialService.referentialLoaded$.next(true);
		fixture.detectChanges();
		const localOnly = fixture.debugElement.nativeElement.querySelector('#localOnly');
		expect(localOnly).not.toBeNull();
		const openidOauth = fixture.debugElement.nativeElement.querySelector('#openidOauth');
		expect(openidOauth).toBeNull();
	});

	it('should display the OpenId panel if an openId authentication server has been declared.', () => {

		referentialService.openidServers = [];
		referentialService.openidServers.push(new OpenidServer());
		referentialService.referentialLoaded$.next(true);
		googleService.register();

		fixture.detectChanges();

		const localOnly = fixture.debugElement.nativeElement.querySelector('#localOnly');
		expect(localOnly).toBeNull();
		const openidOauth = fixture.debugElement.nativeElement.querySelector('#openidOauth');
		expect(openidOauth).not.toBeNull();

	});

	it('should connect correctly the end-user with the OpenID process.', () => {
		referentialService.openidServers = [];
		referentialService.openidServers.push(new OpenidServer());
		referentialService.referentialLoaded$.next(true);

		const staff = new Collaborator();
		const spy1 = spyOn(authService, 'connectOpenId$').and.returnValue(of(new OpenIdTokenStaff(null, staff)));
		const spy2 = spyOn(tokenService, 'saveToken').and.returnValue(null);
		const spy3 = spyOn(authService, 'setConnect').and.returnValue(null);
		const spy4 = spyOn(projectService, 'startLoadingProjects').and.returnValue(null);
		const spy5 = spyOn(staffListService, 'startLoadingStaff').and.returnValue(null);

		googleService.googleToken = new GoogleToken();
		googleService.googleToken.name = 'Frédéric VIDAL';
		googleService.register();
		googleService.signIn();

		fixture.detectChanges();

		expect(spy1).toHaveBeenCalled();
		expect(spy2).toHaveBeenCalled();
		expect(spy3).toHaveBeenCalled();
		expect(spy4).toHaveBeenCalled();
		expect(spy5).toHaveBeenCalled();

	});

});
