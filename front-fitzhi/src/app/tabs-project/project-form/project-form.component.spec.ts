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
import { environment } from 'src/environments/environment';

describe('ProjectFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let listProjectsService: ListProjectsService;
	let backendSetupService: BackendSetupService;
	let httpTestingController: HttpTestingController;

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

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};
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

	it('Should be created without error.', () => {
		expect(component).toBeTruthy();
	});

	it('Clicking the button "buttonOk" execute a CREATE rest API call.', () => {

		const spy = spyOn(projectService, 'createNewProject$').and.callThrough();

		// We force to a new project.
		projectService.project.id = -1;

		fixture.detectChanges();
		const button = fixture.debugElement.nativeElement.querySelector('#buttonOk');
		button.click();

		const creationUrl = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		expect(creationUrl.request.method).toEqual('POST');
		creationUrl.flush(null);

		fixture.detectChanges();
		expect(component).toBeTruthy();

	});

	it('Should be created without error.', () => {
		expect(component).toBeTruthy();
	});

	it('Clicking the button "buttonOk" execute an UPDATE rest API call.', () => {

		const spyUpdate = spyOn(projectService, 'updateCurrentProject$').and.callThrough();

		fixture.detectChanges();
		const button = fixture.debugElement.nativeElement.querySelector('#buttonOk');
		button.click();

		const creationUrl = httpTestingController.expectOne('URL_OF_SERVER/api/project/1789');
		expect(creationUrl.request.method).toEqual('PUT');
		creationUrl.flush(null);

	});

	it(`Updating the project invokes a reload of the filtered projects list (${environment.autoConnect}).`, () => {

		const spyUpdate = spyOn(projectService, 'updateCurrentProject$').and.returnValue(of(true));
		const spyReload = spyOn(listProjectsService, 'reload').and.returnValue();

		fixture.detectChanges();
		const button = fixture.debugElement.nativeElement.querySelector('#buttonOk');
		button.click();

		expect(spyReload).toHaveBeenCalled();
	});

	afterEach(() => {
		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
