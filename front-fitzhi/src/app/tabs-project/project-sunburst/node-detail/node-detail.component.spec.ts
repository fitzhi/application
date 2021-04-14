import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { NodeDetailComponent } from './node-detail.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { ListFilenamesComponent } from './list-filenames/list-filenames.component';
import { InitTest } from 'src/app/test/init-test';
import { ListContributorsComponent } from './list-contributors/list-contributors.component';

describe('NodeDetailComponent', () => {
	let component: NodeDetailComponent;
	let fixture: ComponentFixture<NodeDetailComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ NodeDetailComponent, ListFilenamesComponent],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(NodeDetailComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
