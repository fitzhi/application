import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { StaffProjectsComponent } from './staff-projects.component';
import { InitTest } from 'src/app/test/init-test';

describe('StaffProjectsComponent', () => {
	let component: StaffProjectsComponent;
	let fixture: ComponentFixture<StaffProjectsComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffProjectsComponent, StaffProjectsComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffProjectsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
