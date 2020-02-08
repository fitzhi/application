import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { InitTest } from 'src/app/test/init-test';

describe('TableGhostsComponent', () => {
	let component: TableGhostsComponent;
	let fixture: ComponentFixture<TableGhostsComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TableGhostsComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TableGhostsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
