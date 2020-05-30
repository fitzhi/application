import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectRemoveComponent } from './project-remove.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';

describe('ProjectRemoveComponent', () => {
	let component: ProjectRemoveComponent;
	let fixture: ComponentFixture<ProjectRemoveComponent>;
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectRemoveComponent ],
			providers: [ProjectService, ReferentialService],
			imports: [ HttpClientTestingModule, MatDialogModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		httpTestingController = TestBed.get(HttpTestingController);

		backendSetupService = TestBed.get(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		fixture = TestBed.createComponent(ProjectRemoveComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
		reqSkill.flush([]);
	});

	it('should call projectService.removeProject when the button Remove is clicked', () => {

		const projectService = TestBed.get(ProjectService);
		projectService.allProjects = [];
		expect(projectService.allProjects.length).toEqual(0);

		const spy = spyOn(projectService, 'removeProject').and.callThrough();

		projectService.allProjects.push(new Project(1515, 'Marignan'));
		projectService.project = new Project(1789, 'Revolutionary project');
		projectService.allProjects.push(projectService.project);
		expect(projectService.allProjects.length).toEqual(2);

		const button = fixture.debugElement.nativeElement.querySelector('button');
		button.click();
		fixture.detectChanges();
		expect(component).toBeTruthy();

		const reqSkill = httpTestingController.expectOne('URL_OF_SERVER/api/skill/all');
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
