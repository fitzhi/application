import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { ProjectStaffComponent } from './project-staff.component';
import { Component, ViewChild } from '@angular/core';
import { Project } from 'src/app/data/project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { InitTest } from 'src/app/test/init-test';
import { ProjectService } from 'src/app/service/project.service';
import { Constants } from 'src/app/constants';
import { HttpTestingController } from '@angular/common/http/testing';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';

describe('ProjectStaffComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let httpMock: HttpTestingController;

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

		@ViewChild(ProjectStaffComponent, {static: false}) projectStaffComponent: ProjectStaffComponent;
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata  =  {
			declarations: [TestHostComponent, ProjectStaffComponent ],
			providers: [CinematicService, ProjectService],
			imports: [MatTableModule, MatPaginatorModule, MatSortModule]
		};

		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

	}));

	beforeEach(() => {
		httpMock = TestBed.get(HttpTestingController);
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		const req = httpMock.expectOne('http://localhost:8080/api/skill/all');
		expect(req.request.method).toBe('GET');
		req.flush(mockSkills);

		fixture.detectChanges();
	});

	it('We should create', () => {
		expect(component).toBeTruthy();
	});

	it('We do not create the dataSource as long as the end-user did not click on the \'staff\' tab', () => {
		expect(component.projectStaffComponent.dataSource).toBeUndefined();
	});

	it('We do not create the dataSource as long as the application did not load a project', () => {
		const cinematicService = TestBed.get(CinematicService);
		cinematicService.tabProjectActivated$.next(Constants.PROJECT_IDX_TAB_STAFF);
		fixture.detectChanges();
		expect(component.projectStaffComponent.dataSource).toBeUndefined();
	});

	it('We do not create the dataSource as long as the application did not load a project', () => {
		const cinematicService = TestBed.get(CinematicService);
		cinematicService.tabProjectActivated$.next(Constants.PROJECT_IDX_TAB_STAFF);

		fixture.detectChanges();
		expect(component.projectStaffComponent.dataSource).toBeUndefined();
	});

	it('We create the dataSource when the project has been loaded, and when the dedicated tab has been clicked', () => {
		const cinematicService = TestBed.get(CinematicService);
		cinematicService.tabProjectActivated$.next(Constants.PROJECT_IDX_TAB_STAFF);

		const projectService = TestBed.get(ProjectService);
		projectService.project = new Project(1789, 'the revolutionary project');
		projectService.projectLoaded$.next(true);

		const req = httpMock.expectOne('http://localhost:8080/api/project/contributors/1789');
		expect(req.request.method).toBe('GET');
		req.flush(mockContributorDTO);

		expect(component.projectStaffComponent.dataSource).toBeDefined();
		expect(2).toEqual(component.projectStaffComponent.dataSource.data.length);
		fixture.detectChanges();
	});

	afterEach(() => {
		httpMock.verify();
	});


});