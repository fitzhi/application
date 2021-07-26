import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProjectRemoveComponent } from './project-remove.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('ProjectRemoveComponent', () => {
	let component: ProjectRemoveComponent;
	let fixture: ComponentFixture<ProjectRemoveComponent>;
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectRemoveComponent ],
			providers: [ProjectService, ReferentialService, CinematicService],
			imports: [ HttpClientTestingModule, MatDialogModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		httpTestingController = TestBed.inject(HttpTestingController);

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		fixture = TestBed.createComponent(ProjectRemoveComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill');
		reqSkill.flush([]);
	});

	it('should call projectService.removeProject when the button Remove is clicked', () => {

		const projectService = TestBed.inject(ProjectService);
		projectService.allProjects = [];
		expect(projectService.allProjects.length).toEqual(0);

		const spy = spyOn(projectService, 'removeApiProject$').and.callThrough();

		projectService.allProjects.push(new Project(1515, 'Marignan'));
		projectService.project = new Project(1789, 'Revolutionary project');
		projectService.allProjects.push(projectService.project);
		expect(projectService.allProjects.length).toEqual(2);

		const button = fixture.debugElement.nativeElement.querySelector('button');
		button.click();
		fixture.detectChanges();
		expect(component).toBeTruthy();

		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill');
		const reqDelete = httpTestingController.expectOne('URL_OF_SERVER/api/project/1789');
		expect(reqDelete.request.method).toEqual('DELETE');
		reqDelete.flush(null);
		reqSkill.flush([]);

		expect(projectService.allProjects.length).toEqual(1);
		expect(projectService.project.id).toEqual(-1);

	});

	afterEach(() => {
		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
