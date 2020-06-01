import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BehaviorSubject } from 'rxjs';
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

describe('ProjectFormComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let backendSetupService: BackendSetupService;
	let httpTestingController: HttpTestingController;

	@Component({
		selector: 'app-project-component',
		template: 	'<app-project-form ' +
						'[risk$]="risk$" >' +
					'</app-project-form>'
	})
	class TestHostComponent {
		public risk$ = new BehaviorSubject<number>(1);
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectFormComponent, TechxhiMedalComponent, QuotationBadgeComponent, AuditGraphicBadgeComponent,
				TestHostComponent],
			providers: [ReferentialService, CinematicService],
			imports: [
					MatButtonToggleModule, MatCheckboxModule, HttpClientTestingModule, FormsModule, ReactiveFormsModule,
					MatDialogModule, RouterTestingModule
				]
		})
		.compileComponents();
	}));

	beforeEach(() => {

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

	it('should be created without any error', () => {
		expect(component).toBeTruthy();
	});

	it('Creation of a new project', () => {
		const spy = spyOn(projectService, 'createNewProject').and.callThrough();

		// We force to a new project.
		projectService.project.id = -1;

		fixture.detectChanges();
		const button = fixture.debugElement.nativeElement.querySelector('#buttonOk');
		button.click();

		const creationUrl = httpTestingController.expectOne('URL_OF_SERVER/api/project');
		creationUrl.flush(null);

		fixture.detectChanges();
		expect(component).toBeTruthy();
	});

	it('Update of an existing project', () => {
		const spy = spyOn(projectService, 'updateCurrentProject').and.callThrough();

		fixture.detectChanges();
		const button = fixture.debugElement.nativeElement.querySelector('#buttonOk');
		button.click();

		const creationUrl = httpTestingController.expectOne('URL_OF_SERVER/api/project/1789');
		creationUrl.flush(null);

		fixture.detectChanges();
		expect(component).toBeTruthy();
	});

	afterEach(() => {
		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
