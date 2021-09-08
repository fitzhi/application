import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { InitTest } from 'src/app/test/init-test';
import { Component, ViewChild } from '@angular/core';
import { BehaviorSubject, of } from 'rxjs';
import { Unknown } from '../../../../data/unknown';
import { Collaborator } from 'src/app/data/collaborator';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { Project } from 'src/app/data/project';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { By } from '@angular/platform-browser';
import { ProjectService } from 'src/app/service/project/project.service';
import { SunburstCacheService } from '../../service/sunburst-cache.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';

describe('TableGhostsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let sunburstCacheService: SunburstCacheService;
	let projectService: ProjectService;
	let staffListService: StaffListService;

	const allStaff = [
		{
			idStaff: 1964,
			login: 'frvidal',
			firstName: 'Frédéric',
			lastName: 'VIDAL',
			nickName: 'frvidal',
			email: 'frederic.vidal@fitzhi.com',
			level: 'Developper',
			external: false,
			forceActiveState: false,
			active: true,
			dateInactive: null,
			experiences: [],
			missions: []
		}
	];

	@Component({
		selector: 'app-host-component',
		template:
			`
			<h2 style="margin: 10px">The Ghosts...</h2>
			<div style="width:80%;height:50%">
				<app-table-ghosts [dataSourceGhosts$]="dataSourceGhosts$">
				</app-table-ghosts>
			</div>
			`
	})
	class TestHostComponent {

		public dataSourceGhosts$: BehaviorSubject<ProjectGhostsDataSource>;

		public projectGhostsDataSource: ProjectGhostsDataSource;

		@ViewChild(TableGhostsComponent) tableGhostsComponent: TableGhostsComponent;

		constructor(ps: ProjectService) {

			ps.project = new Project(1789, 'Revolutionary project');

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
						lastCommit: new Date('2021-1-7'),
						numberOfCommits: 117,
						numberOfFiles: 83
					},
					{
						idStaff: 1964,
						pseudo: 'frvidal',
						login: 'frvidal	',
						firstname: 'Frédéric',
						lastname: 'VIDAL',
						fullName: '',
						technical: false,
						active: false,
						external: false,
						action: '',
						staffRelated: new Collaborator(),
						staffRecorded: false,
						lastCommit: new Date('2021-12-25'),
						numberOfCommits: 10,
						numberOfFiles: 9
					},
				],
				allStaff
			);
			this.dataSourceGhosts$ = new BehaviorSubject(this.projectGhostsDataSource);
		}
	}

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, TableGhostsComponent],
			providers: [StaffService, ProjectService, SunburstCacheService, ProjectService, StaffListService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		sunburstCacheService = TestBed.inject(SunburstCacheService);
		projectService = TestBed.inject(ProjectService);
		staffListService = TestBed.inject(StaffListService);
		fixture.detectChanges();
	});

	it('Disconnect a ghost from an existing developer frvidal for the given login', () => {

		const buttonAddStaff: HTMLInputElement = fixture.debugElement.query(By.css('#addStaff-1')).nativeElement;

		const spyClearCache = spyOn(sunburstCacheService, 'clearReponse');
		const spyUpdateGhost$ = spyOn(projectService, 'updateGhost$').and.returnValue(of(true));

		staffListService.allStaff$.next(allStaff);

		const login: HTMLInputElement = fixture.debugElement.query(By.css('#login-1')).nativeElement;
		expect(login).toBeDefined();
		login.value = '';
		login.dispatchEvent(new Event('input'));
		fixture.detectChanges();
		fixture.detectChanges();

		expect(spyClearCache).toHaveBeenCalled();
		expect(spyUpdateGhost$).toHaveBeenCalled();

		component.tableGhostsComponent.renderRows();
		fixture.detectChanges();

		expect(fixture.debugElement.query(By.css('#firstname-1')).nativeElement.value).toBe('');
		expect(fixture.debugElement.query(By.css('#lastname-1')).nativeElement.value).toBe('');

		/*
		expect(buttonAddStaff.disabled).toBeTrue();
*/
	});

});

