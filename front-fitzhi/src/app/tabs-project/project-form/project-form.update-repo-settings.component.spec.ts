import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BehaviorSubject, of } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project/project.service';
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
import { BranchComponent } from './branch/branch.component';
import { ListProjectsService } from '../list-project/list-projects-service/list-projects.service';
import { DeclaredSonarServer } from 'src/app/data/declared-sonar-server';
import { By } from '@angular/platform-browser';
import { SunburstCacheService } from '../project-sunburst/service/sunburst-cache.service';
import { expressionType } from '@angular/compiler/src/output/output_ast';

describe('ProjectFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let listProjectsService: ListProjectsService;
	let backendSetupService: BackendSetupService;
	let httpTestingController: HttpTestingController;
	let referentialService: ReferentialService;
	let sunburstCacheService: SunburstCacheService;

	@Component({
		selector: 'app-project-component',
		template: 	'<app-project-form ' +
						'[risk$]="risk$" *ngIf="(prjService.project)">' +
					'</app-project-form>'
	})
	class TestHostComponent {
		constructor(public prjService: ProjectService) {}
		public risk$ = new BehaviorSubject<number>(1);
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectFormComponent, TechxhiMedalComponent, QuotationBadgeComponent, AuditGraphicBadgeComponent,
				TestHostComponent, BranchComponent],
			providers: [ReferentialService, CinematicService, ProjectService, ListProjectsService],
			imports: [
					MatButtonToggleModule, MatCheckboxModule, HttpClientTestingModule, FormsModule, ReactiveFormsModule,
					MatDialogModule, RouterTestingModule
				]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		httpTestingController = TestBed.inject(HttpTestingController);

		listProjectsService = TestBed.inject(ListProjectsService);
		referentialService = TestBed.inject(ReferentialService);
		sunburstCacheService = TestBed.inject(SunburstCacheService);

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};
		project.connectionSettings = 3; // NO_USER_PASSWORD_ACCESS
		project.urlRepository = 'Url repository';
		projectService = TestBed.inject(ProjectService);
		projectService.project = project;
		projectService.projectLoaded$ = new BehaviorSubject(true);

		// We do not need the handle the skill retrieval.
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill');
		reqSkill.flush([
			{
				id: 1,
				title: 'Java'
			}
		]);

		fixture.detectChanges();
	});

	it('Should cleanup the Sunburst cache data when the user change the selected branch.', () => {
		expect(component).toBeTruthy();

		const spyClearSessionStorage = spyOn(sunburstCacheService, 'clearResponse').and.callThrough();
		sunburstCacheService.saveResponse('response');
		expect(sunburstCacheService.getResponse()).toEqual('response');

		// We mock the branches loading
		const reqBranches = httpTestingController.expectOne('URL_OF_SERVER/api/project/1789/branches');
		reqBranches.flush([ 'Branch One', 'Branch Two' ]);

		spyOn(projectService, 'saveSonarUrl$').and.returnValue(of(true));

		referentialService.sonarServers$.next([new DeclaredSonarServer('Sonar server one')]);
		fixture.detectChanges();

		const branch = fixture.debugElement.query(By.css('#branch')).nativeElement;
		expect(branch).toBeDefined();

		branch.value = branch.options[1].value;
		branch.dispatchEvent(new Event('change'));
		fixture.detectChanges();

		expect(spyClearSessionStorage).toHaveBeenCalled();
		expect(sunburstCacheService.getResponse()).toBeNull();

	});

	it('Should cleanup the Sunburst cache data when the user update the repository url.', () => {
		expect(component).toBeTruthy();

		const spyClearSessionStorage = spyOn(sunburstCacheService, 'clearResponse').and.callThrough();
		sunburstCacheService.saveResponse('response');
		expect(sunburstCacheService.getResponse()).toEqual('response');

		// We mock the branches loading
		const reqBranches = httpTestingController.expectOne('URL_OF_SERVER/api/project/1789/branches');
		reqBranches.flush([ 'Branch One', 'Branch Two' ]);

		spyOn(projectService, 'saveSonarUrl$').and.returnValue(of(true));
		referentialService.sonarServers$.next([new DeclaredSonarServer('Sonar server one')]);
		fixture.detectChanges();

		const urlRepositoryInput = fixture.debugElement.query(By.css('#urlRepository'));
		console.log ('Former url', urlRepositoryInput.nativeElement.value);
		urlRepositoryInput.triggerEventHandler('blur', {target: {value: 'https://github.com/fitzhi/application' }} );
		fixture.detectChanges();

		fixture.detectChanges();

		expect(spyClearSessionStorage).toHaveBeenCalled();
		expect(sunburstCacheService.getResponse()).toBeNull();

	});

});
