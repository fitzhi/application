import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { ProjectGhostsComponent } from './project-ghosts.component';
import { TableGhostsComponent } from './table-ghosts/table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { InitTest } from 'src/app/test/init-test';
import { BehaviorSubject } from 'rxjs';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { ViewChild, Component } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';
import { Project } from 'src/app/data/project';
import { Collaborator } from 'src/app/data/collaborator';
import { GhostsService } from './service/ghosts.service';

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



	beforeEach(async(() => {
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
