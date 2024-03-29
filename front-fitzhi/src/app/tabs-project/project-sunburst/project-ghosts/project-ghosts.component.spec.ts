import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { Collaborator } from 'src/app/data/collaborator';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project/project.service';
import { InitTest } from 'src/app/test/init-test';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { ProjectGhostsComponent } from './project-ghosts.component';
import { GhostsService } from './service/ghosts.service';
import { TableGhostsComponent } from './table-ghosts/table-ghosts.component';


describe('ProjectGhostsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;


	@Component({
		selector: 'app-host-component',
		template:
			`
			<div style="width:80%;height:50%">
				<app-project-ghosts [dataSourceGhosts$]="dataSourceGhosts$">
				</app-project-ghosts>
			</div>
			`
	})
	class TestHostComponent {

		public dataSourceGhosts$: BehaviorSubject<ProjectGhostsDataSource>;

		public projectGhostsDataSource: ProjectGhostsDataSource;

		@ViewChild(ProjectGhostsComponent) projectGhostsComponent: ProjectGhostsComponent;

		constructor(projectService: ProjectService) {

			projectService.project = new Project(1789, 'Revolutionary project');

			this.projectGhostsDataSource = new ProjectGhostsDataSource(
				[
					{
						idStaff: -1,
						pseudo: 'chaddock',
						login: '',
						firstname: 'Captain',
						lastname: 'Haddock',
						fullName: '',
						technical: false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,
					},
					{
						idStaff: -1,
						pseudo: 'frvidal',
						login: '',
						firstname: 'f',
						lastname: 'l',
						fullName: '',
						technical: false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,
					},
				],
				[]
			);
			this.dataSourceGhosts$ = new BehaviorSubject(this.projectGhostsDataSource);
		}
	}



	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, ProjectGhostsComponent, TableGhostsComponent],
			providers: [GhostsService, ProjectService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
		expect(component.projectGhostsComponent).toBeDefined();
	});
});
