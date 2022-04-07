import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { OpenidServer } from 'src/app/data/openid-server';
import { AlternativeOpenidConnectionComponent } from 'src/app/interaction/alternative-openid-connection/alternative-openid-connection.component';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { ConnectUserFormComponent } from './connect-user-form/connect-user-form.component';
import { ConnectUserComponent } from './connect-user.component';


describe('ConnectUserComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let referentialService: ReferentialService;
	let googleService: GoogleService;

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
			declarations: [ ConnectUserFormComponent, ConnectUserComponent, TestHostComponent, AlternativeOpenidConnectionComponent ],
			providers: [ProjectService, CinematicService, FileService, MessageBoxService, FormBuilder, ReferentialService, GoogleService],
			imports: [HttpClientTestingModule, MatDialogModule, RouterTestingModule, FormsModule, ReactiveFormsModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		referentialService = TestBed.inject(ReferentialService);
		googleService = TestBed.inject(GoogleService);
		fixture.detectChanges();
	});
/*
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
*/
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

});
