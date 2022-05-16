import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AfterViewInit, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GithubService } from 'src/app/service/github/github.service';
import { GoogleService } from 'src/app/service/google/google.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AlternativeOpenidConnectionComponent } from './alternative-openid-connection.component';


describe('Github OpenidConnectionComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="width: 400px; height: 300px; background-color: lightYellow; margin-top: 30px; margin-left: 100px; padding: 20px">
				<app-alternative-openid-connection></app-alternative-openid-connection>
			</div>`})

	class TestHostComponent implements AfterViewInit {

		constructor() {}

		ngAfterViewInit(): void {
		}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ AlternativeOpenidConnectionComponent, TestHostComponent ],
			providers: [ReferentialService, GoogleService, GithubService],
			imports: [HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule]

		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		const githubService = TestBed.inject(GithubService);
		fixture.detectChanges();
	});

	it('should be created without error.', () => {
		expect(component).toBeTruthy();
		const localOauthOnly = fixture.debugElement.nativeElement.querySelector('#localOauthOnly');
		expect(localOauthOnly).toBeNull();
	});

	it('should display the Github button, if Github is an available authentication server.', () => {

		const githubService = TestBed.inject(GithubService);
		githubService.clientId = 'myClientId';
		githubService.register();
		fixture.detectChanges();

		const btnGithub = fixture.debugElement.nativeElement.querySelector('#btnGithub');
		expect(btnGithub).not.toBeNull();
	});

});
