import { Component } from '@angular/core';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { MatTableDataSource } from '@angular/material/table';
import { Filename } from 'src/app/data/filename';
import { InitTest } from 'src/app/test/init-test';
import { ListFilenamesComponent } from './list-filenames.component';


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

	beforeEach(waitForAsync(() => {
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
