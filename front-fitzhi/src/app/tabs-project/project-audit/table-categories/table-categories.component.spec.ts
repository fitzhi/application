import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Project } from 'src/app/data/project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { ProjectAuditService } from '../service/project-audit.service';
import { TableCategoriesComponent } from './table-categories.component';


describe('TableCategoriesComponent', () => {
	let component: TableCategoriesComponent;
	let fixture: ComponentFixture<TableCategoriesComponent>;
	let projectService: ProjectService;
	let referentialService: ReferentialService;
	let projectAuditService: ProjectAuditService;

	beforeEach(waitForAsync(() => {
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

		referentialService = TestBed.inject(ReferentialService);
		referentialService.topics$.next({'1': 'One', '2': 'Two'});

		projectAuditService = TestBed.inject(ProjectAuditService);

		projectService = TestBed.inject(ProjectService);
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionary project';
		project.audit = {};
		projectService.project = project;
		projectService.projectLoaded$.next(true);

		fixture.detectChanges();
	});

	it('should be created successfully.', () => {
		expect(component).toBeTruthy();
	});

	it('should add correctly a new topic in the audit.', () => {
		const spy = spyOn(projectAuditService, 'updateTopic').and.returnValue(null);
		const cbTopic = fixture.debugElement.query(By.css('#select-1'));
		expect(cbTopic).toBeDefined();
		cbTopic.triggerEventHandler('change', {});
		fixture.detectChanges();

		expect(spy).toHaveBeenCalled();
	});

});
