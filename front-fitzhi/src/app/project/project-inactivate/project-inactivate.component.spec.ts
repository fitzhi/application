import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectInactivateComponent } from './project-inactivate.component';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { By } from '@angular/platform-browser';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('ProjectInactivateComponent', () => {
	let component: ProjectInactivateComponent;
	let fixture: ComponentFixture<ProjectInactivateComponent>;
	let projectService: ProjectService;
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectInactivateComponent ],
			providers: [ProjectService, ReferentialService, BackendSetupService, CinematicService],
			imports: [ HttpClientTestingModule, MatDialogModule ]

		})
		.compileComponents();
	}));

	beforeEach(() => {

		backendSetupService = TestBed.get(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		fixture = TestBed.createComponent(ProjectInactivateComponent);
		component = fixture.componentInstance;

		httpTestingController = TestBed.get(HttpTestingController);

		projectService = TestBed.get(ProjectService);
		projectService.project = new Project(1066, 'Hasting');
		projectService.allProjects = [];
		projectService.allProjects.push(projectService.project);

		fixture.detectChanges();
	});

	it('should be created without error', () => {
		expect(component).toBeTruthy();

		handleSkills();
	});

	it('The DIV in charge of INactivation should be created in the DOM', () => {
		projectService.project.active = true;
		fixture.detectChanges();
		expect(fixture.debugElement.query(By.css('.project-reactivate'))).toBeNull();
		expect(fixture.debugElement.query(By.css('.project-inactivate'))).toBeDefined();

		// We do not need the handle the skill retrieval.
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
		reqSkill.flush([]);
	});

	it('The DIV in charge of REactivation should be created in the DOM', () => {
		projectService.project.active = false;
		fixture.detectChanges();
		expect(fixture.debugElement.query(By.css('.project-inactivate'))).toBeNull();
		expect(fixture.debugElement.query(By.css('.project-reactivate'))).toBeDefined();

		// We do not need the handle the skill retrieval.
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
		reqSkill.flush([]);
	});

	it('IN-activating the project', () => {
		projectService.project.active = true;
		fixture.detectChanges();

		const spy = spyOn(projectService, 'inactivateProject').and.callThrough();

		const button = fixture.debugElement.nativeElement.querySelector('button');
		button.click();
		fixture.detectChanges();
		expect(component).toBeTruthy();

		const postInactivate = httpTestingController.expectOne('URL_OF_SERVER/api/project/rpc/inactivation/1066');
		expect(postInactivate.request.method).toEqual('POST');
		postInactivate.flush(null);

		expect(projectService.project.active).toEqual(false);

		handleSkills();
	});

	it('RE-activating the project', () => {
		projectService.project.active = false;
		fixture.detectChanges();

		const spy = spyOn(projectService, 'reactivateProject').and.callThrough();

		const button = fixture.debugElement.nativeElement.querySelector('button');
		button.click();
		fixture.detectChanges();
		expect(component).toBeTruthy();

		const postInactivate = httpTestingController.expectOne('URL_OF_SERVER/api/project/rpc/reactivation/1066');
		expect(postInactivate.request.method).toEqual('POST');
		postInactivate.flush(null);

		handleSkills();

		expect(projectService.project.active).toEqual(true);

	});

	function handleSkills() {
		// We do not need the handle the skill retrieval.
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
		reqSkill.flush([]);
	}

	afterEach(() => {

		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
