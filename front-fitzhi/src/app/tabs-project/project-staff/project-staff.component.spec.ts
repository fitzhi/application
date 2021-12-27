import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { Constants } from 'src/app/constants';
import { Project } from 'src/app/data/project';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { InitTest } from 'src/app/test/init-test';
import { environment } from 'src/environments/environment';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { ProjectStaffComponent } from './project-staff.component';


describe('ProjectStaffComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let httpMock: HttpTestingController;
	let backendSetupService: BackendSetupService;

	const mockContributorDTO = {
		contributors: [
			{
				idStaff: 1,
				fullname: 'Charly Chaplin',
				active: true,
				external: false,
				firstCommit: '2018-02-24',
				lastCommit: '2020-02-24',
				numberOfCommits: 10,
				numberOfFiles: 100
			},
			{
				idStaff: 2,
				fullname: 'James Bond',
				active: true,
				external: false,
				firstCommit: '1964-02-08',
				lastCommit: '2020-02-24',
				numberOfCommits: 20,
				numberOfFiles: 150
			}
		]
	};
	const mockSkills = [
		{
			id: 1,
			fullname: 'Java'
		}
	];

	@Component({
		selector: 'app-host-component',
		template: 	'<app-project-staff></app-project-staff>'
	})
	class TestHostComponent {

		@ViewChild(ProjectStaffComponent) projectStaffComponent: ProjectStaffComponent;
	}

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata  =  {
			declarations: [TestHostComponent, ProjectStaffComponent ],
			providers: [CinematicService, ProjectService, BackendSetupService, ProjectStaffService],
			imports: [MatTableModule, MatPaginatorModule, MatSortModule, HttpClientTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		backendSetupService = TestBed.inject(BackendSetupService);
		spyOn(backendSetupService, 'hasSavedAnUrl').and.returnValue(true);
		spyOn(backendSetupService, 'url').and.returnValue(environment.apiUrl + '/api');

	}));

	beforeEach(() => {
		httpMock = TestBed.inject(HttpTestingController);
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be instanciated successfully.', () => {
		expect(component).toBeTruthy();
	});

	it('should not create the dataSource as long as the end-user did not click on the \'staff\' tab.', () => {
		expect(component.projectStaffComponent.dataSource).toBeUndefined();
	});

	it('should not create the dataSource as long as the application did not load a project', () => {
		const cinematicService = TestBed.inject(CinematicService);
		cinematicService.tabProjectActivatedSubject$.next(Constants.PROJECT_IDX_TAB_STAFF);
		fixture.detectChanges();
		expect(component.projectStaffComponent.dataSource).toBeUndefined();
	});

	it('should create the dataSource when the project has been loaded, and when the dedicated tab has been clicked', () => {
		const cinematicService = TestBed.inject(CinematicService);
		cinematicService.tabProjectActivatedSubject$.next(Constants.PROJECT_IDX_TAB_STAFF);

		const projectService = TestBed.inject(ProjectService);
		projectService.project = new Project(1789, 'the revolutionary project');
		projectService.projectLoaded$.next(true);

		let req = httpMock.expectOne('URL_OF_SERVER/api/project/1789/contributors');
		expect(req.request.method).toBe('GET');
		req.flush(mockContributorDTO);

		expect(component.projectStaffComponent.dataSource).toBeDefined();
		expect(2).toEqual(component.projectStaffComponent.dataSource.data.length);
		fixture.detectChanges();

		req = httpMock.expectOne('URL_OF_SERVER/api/skill');
		expect(req.request.method).toBe('GET');
		req.flush(mockSkills);

		httpMock.verify();
	});


});
