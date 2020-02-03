import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableCategoriesComponent } from './table-categories.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { Observable, BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';

describe('TableCategoriesComponent', () => {
	let component: TableCategoriesComponent;
	let fixture: ComponentFixture<TableCategoriesComponent>;
	let projectService: ProjectService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TableCategoriesComponent);
		component = fixture.componentInstance;
		projectService = TestBed.get(ProjectService);
		const project = new Project();
		project.id = 1789;
		project.name = 'Revolutionnary project';
		project.audit = {};
		projectService.projectLoaded$.next(false);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
