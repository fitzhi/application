import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { ListContributorsComponent } from './list-contributors.component';
import { InitTest } from 'src/app/test/init-test';
import { Component } from '@angular/core';
import { Contributor } from 'src/app/data/contributor';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';

describe('ListContributorsComponent (5 elements)', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
    template: `
		<div style="height: 600px; width: 500px; background-color: lightGrey">
			<app-list-contributors [contributors]="contributors">
			</app-list-contributors>
		</div>`
	})
	class TestHostComponent {
		public contributors = new MatTableDataSource<Contributor>();
		constructor() {
			localStorage.setItem('list-contributors.pageSize', '5');
			for (let index = 0; index < 15; index++) {
				const contributor = new Contributor();
				contributor.idStaff = index;
				contributor.fullname = 'Fullname ' + index;
				this.contributors.data.push(contributor);
			}
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ListContributorsComponent, TestHostComponent],
			imports: [MatTableModule, MatPaginatorModule, BrowserAnimationsModule]
		};
		InitTest.addImports(testConf.imports);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should display 5 contributors', () => {
		expect(component).toBeTruthy();
		for (let i = 0; i < 5; i++) {
			const contributor = fixture.debugElement.query(By.css('#contributor-' + i));
			expect(contributor).not.toBeNull();
		}
		// The project 5 is not present
		const contributor = fixture.debugElement.query(By.css('#contributor-5'));
		expect(contributor).toBeNull();
	});


});
