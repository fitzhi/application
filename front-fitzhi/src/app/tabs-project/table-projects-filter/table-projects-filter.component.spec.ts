import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 250px; height: 400px">
	  					<app-table-projects-filter (messengerFilteredProjects)="onChangeFilteredProjects($event)"></app-table-projects-filter>
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

	it('should inform the parent container with the selected projects.', fakeAsync(() => {
		fixture.detectChanges();

		// We deselect all projects.
		fixture.debugElement.query(By.css('#name--1')).nativeElement.click();
		tick();
		fixture.detectChanges();

		// We select 2 projects.
		fixture.debugElement.query(By.css('#name-3')).nativeElement.click();
		tick();
		fixture.debugElement.query(By.css('#name-7')).nativeElement.click();
		tick();
		fixture.detectChanges();

		// We verify that the 2 projects have been emitted to the parent container.
		expect(component.projects.length).toBe(2);
		expect(component.projects[0].id).toBe(3);
		expect(component.projects[1].id).toBe(7);
	}));

});

