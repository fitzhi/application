import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableCategoriesComponent } from './table-categories.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { Observable } from 'rxjs';

describe('TableCategoriesComponent', () => {
	let component: TableCategoriesComponent;
	let fixture: ComponentFixture<TableCategoriesComponent>;

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
		component.project$ = new Observable();
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
