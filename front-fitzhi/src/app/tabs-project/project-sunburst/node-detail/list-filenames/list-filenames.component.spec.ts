import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { ListFilenamesComponent } from './list-filenames.component';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InitTest } from 'src/app/test/init-test';
import { Component } from '@angular/core';
import { FilenamesDataSource } from '../filenames-data-source';
import { Filename } from 'src/app/data/filename';


describe('ListFilenamesComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 400px; height: 400px; background-color: lightGrey">
						<app-list-filenames [filenames]="filenames" >
						</app-list-filenames>
					</div>`
	})
	class TestHostComponent {
		public filenames = new MatTableDataSource<Filename>();

		constructor() {
			this.filenames.data =
			[
				new Filename('one', new Date()),
				new Filename('two', new Date()),
				new Filename('three', new Date()),
				new Filename('four', new Date()),
				new Filename('five', new Date()),
				new Filename('six', new Date()),
				new Filename('seven', new Date())
			];
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, ListFilenamesComponent],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
