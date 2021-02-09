import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { ListContributorsComponent } from './list-contributors.component';
import { MatTableModule } from '@angular/material/table';
import { InitTest } from 'src/app/test/init-test';

describe('ListContributorsComponent', () => {
	let component: ListContributorsComponent;
	let fixture: ComponentFixture<ListContributorsComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ListContributorsComponent],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ListContributorsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
