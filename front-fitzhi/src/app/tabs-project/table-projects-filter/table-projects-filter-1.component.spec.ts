import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TreemapProjectsService } from 'src/app/dashboard/treemap-projects/treemap-projects-service/treemap-projects.service';
import { Project } from 'src/app/data/project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { FilteredProject } from './filtered-project';
import { TableProjectsFilterComponent } from './table-projects-filter.component';


describe('TableProjectsFilterComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;
	let treemapProjectsService: TreemapProjectsService;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 250px; height: 400px">
	  					<app-table-projects-filter></app-table-projects-filter>
					</div>`})
	class TestHostComponent implements OnInit {

		public projects = [];

		@ViewChild(TableProjectsFilterComponent) tableProjectsFilterComponent: TableProjectsFilterComponent;

		public onChangeFilteredProjects(projects: FilteredProject[]) {
			this.projects = projects;
		}

		constructor() { }

		ngOnInit(): void {
		}
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [TableProjectsFilterComponent, TestHostComponent],
			providers: [ProjectService, ReferentialService, CinematicService],
			imports: [MatTableModule, MatCheckboxModule, BrowserAnimationsModule, FormsModule,
				HttpClientTestingModule, MatDialogModule]
		})
			.compileComponents();
		projectService = TestBed.inject(ProjectService);
		treemapProjectsService = TestBed.inject(TreemapProjectsService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();

		projectService.allProjects = [];
		for (let i = 0; i < 10; i++) {
			const project = new Project(i, 'Project ' + i);
			project.staffEvaluation = 1;
			projectService.allProjects.push(project);
		}
		treemapProjectsService.idProjects.push(...[-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9]);
		projectService.allProjectsIsLoaded$.next(true);
	});

	it('should be created without error.', done => {
		expect(component).toBeTruthy();
		fixture.detectChanges();
		done();
	});

	it('should de-select correctly a project if the user clicks on the corresponding name.', fakeAsync(() => {

		fixture.detectChanges();

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeTrue();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click();
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeFalse();
	}));

	it('should de-select also the criteria ALL_PROJECTS if the user deselects a project.', fakeAsync(() => {

		fixture.detectChanges();

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === -1);
		expect(prj.selected).toBeTrue();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click();
		tick();
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === -1);
		expect(prj.selected).toBeFalse();

	}));

	it('should select correctly a project if the user clicks on the corresponding name.', fakeAsync(() => {

		let prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		prj.selected = false;
		fixture.detectChanges();

		const name = fixture.debugElement.query(By.css('#name-3'));
		name.nativeElement.click();
		tick();
		fixture.detectChanges();

		prj = component.tableProjectsFilterComponent.dataSource.data.find(p => p.id === 3);
		expect(prj.selected).toBeTrue();
	}));

	it('should de-select ALL projects if the user clicks on the "ALL projects" item.', fakeAsync(() => {
		fixture.detectChanges();
		const name = fixture.debugElement.query(By.css('#name--1'));
		name.nativeElement.click();
		tick();
		fixture.detectChanges();
		component.tableProjectsFilterComponent.dataSource.data.forEach(
			p => {
				expect(p.selected).toBeFalse();
			}
		);
	}));
});

