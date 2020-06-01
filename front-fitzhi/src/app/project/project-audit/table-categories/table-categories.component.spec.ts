import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableCategoriesComponent } from './table-categories.component';
import { Observable, BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('TableCategoriesComponent', () => {
	let component: TableCategoriesComponent;
	let fixture: ComponentFixture<TableCategoriesComponent>;
	let projectService: ProjectService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			providers: [ReferentialService, CinematicService],
			declarations: [ TableCategoriesComponent ],
			imports: [MatTableModule, MatPaginatorModule, MatCheckboxModule, FormsModule, HttpClientTestingModule,
				MatDialogModule, BrowserAnimationsModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TableCategoriesComponent);
		component = fixture.componentInstance;
		projectService = TestBed.get(ProjectService);
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};
		projectService.projectLoaded$.next(false);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
