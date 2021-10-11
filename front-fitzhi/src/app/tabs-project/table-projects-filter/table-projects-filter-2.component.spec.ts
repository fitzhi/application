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
		projectService.allProjectsIsLoaded$.next(true);
	});

	it('should inform the parent container with the selected projects.', fakeAsync(() => {
		fixture.detectChanges();
		const spy = spyOn(treemapProjectsService, 'informSelectedProjects');
		
		// We deselect all projects.
		fixture.debugElement.query(By.css('#name--1')).nativeElement.click();
		tick();
		(<jasmine.Spy>treemapProjectsService.informSelectedProjects).and.returnValue([]);
		fixture.detectChanges();
		expect(spy).toHaveBeenCalled();
		
		// We select 2 projects.
		(<jasmine.Spy>treemapProjectsService.informSelectedProjects).and.returnValue([3, 7]);
		fixture.debugElement.query(By.css('#name-3')).nativeElement.click();
		fixture.debugElement.query(By.css('#name-7')).nativeElement.click();
		tick();
		fixture.detectChanges();
		expect(spy).toHaveBeenCalled();
		
		// We verify that the 2 projects have been emitted to the parent container.
	}));

});

