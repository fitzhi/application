import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BehaviorSubject, of } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { ProjectFormComponent } from './project-form.component';
import { TechxhiMedalComponent } from './techxhi-medal/techxhi-medal.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { QuotationBadgeComponent } from '../project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';
// tslint:disable-next-line:max-line-length
import { AuditGraphicBadgeComponent } from '../project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GitService } from 'src/app/service/git/git.service';
import { Repository } from 'src/app/data/git/repository';
import { BranchComponent } from './branch/branch.component';
import { By } from '@angular/platform-browser';

describe('ProjectFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let gitService: GitService;
	let backendSetupService: BackendSetupService;
	let httpTestingController: HttpTestingController;
	const NO_USER_PASSWORD_ACCESS = 3;

	@Component({
		selector: 'app-project-component',
		template: 	'<app-project-form ' +
						'[risk$]="risk$" *ngIf="(projectService.project)">' +
					'</app-project-form>'
	})
	class TestHostComponent {
		constructor(public projectService: ProjectService) {}
		public risk$ = new BehaviorSubject<number>(1);
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ProjectFormComponent, TechxhiMedalComponent, QuotationBadgeComponent, AuditGraphicBadgeComponent,
				TestHostComponent, BranchComponent],
			providers: [ReferentialService, CinematicService, GitService],
			imports: [
					MatButtonToggleModule, MatCheckboxModule, HttpClientTestingModule, FormsModule, ReactiveFormsModule,
					MatDialogModule, RouterTestingModule
				]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		gitService = TestBed.get(GitService);

		backendSetupService = TestBed.get(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		httpTestingController = TestBed.get(HttpTestingController);

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};
		projectService = TestBed.get(ProjectService);
		projectService.project = project;
		project.urlRepository = null;
		project.connectionSettings = NO_USER_PASSWORD_ACCESS;
	
		projectService.projectLoaded$ = new BehaviorSubject(true);

		// We do not need the handle the skill retrieval.
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
		reqSkill.flush([
			{
				id: 1,
				title: 'Java'
			}
		]);
		fixture.detectChanges();
	});

	it('Handling the GIT repository for a correct URL :-)', async () => {

		const spyConnect = spyOn(gitService, 'connect$')
			.and.callThrough()
			.and.returnValue(of(new Repository()));
		
		const spyBranches = spyOn(gitService, 'branches$')
			.and.callThrough()
			.and.returnValue(of(['master', 'release']));

		const urlRepositoryInput = fixture.debugElement.query(By.css('#urlRepository'));
		console.log ('Former url', urlRepositoryInput.nativeElement.value);
		urlRepositoryInput.triggerEventHandler('blur', 'https://github.com/fitzhi/application' );
		fixture.detectChanges();

		projectService.branches$.subscribe({
			next: branches => {
				expect(branches[0]).toBe('master');
				expect(branches[1]).toBe('release');
			}
		});

	});

	it('Handling the GIT repository for a wrong URL :-(', async(() => {

		expect(component).toBeTruthy();

		// We simulate that the URL is wrong.
		const spyConnect = spyOn(gitService, 'connect$')
			.and.callThrough()
			.and.returnValue(of(null));
		
		// We should not call the method gitService.branches$
		const spyBranches = spyOn(gitService, 'branches$')
			.and.throwError('Should not called branches$');
	
		const urlRepositoryInput = fixture.debugElement.query(By.css('#urlRepository'));
		console.log ('Former url', urlRepositoryInput.nativeElement.value);
		urlRepositoryInput.triggerEventHandler('blur', 'https://github.com/fitzhi/application');
		fixture.detectChanges();
		
		const reqBackend = httpTestingController.expectOne('URL_OF_SERVER/api/project/branches/1789');
		reqBackend.flush(['backend/master']);


	}));

	afterEach(() => {
		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
