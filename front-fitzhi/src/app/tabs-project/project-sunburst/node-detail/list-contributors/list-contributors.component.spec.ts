import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { ListContributorsComponent } from './list-contributors.component';
import { InitTest } from 'src/app/test/init-test';
import { Component, OnInit } from '@angular/core';
import { ContributorsDataSource } from '../contributors-data-source';
import { Contributor } from 'src/app/data/contributor';

describe('ListContributorsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
    template: `
		<div style="height: 200px">
			<app-list-contributors [contributors]="contributors">
			</app-list-contributors>
		</div>`
	})
	class TestHostComponent {
		public contributors = new ContributorsDataSource();
		constructor() {
			const contributors = [];
			for (let index = 0; index < 5; index++) {
				const contributor = new Contributor();
				contributor.idStaff = index;
				contributor.fullname = 'Fullname ' + index;
				contributors.push(contributor);
			}
			this.contributors.sendContributors(contributors);
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ListContributorsComponent, TestHostComponent],
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
