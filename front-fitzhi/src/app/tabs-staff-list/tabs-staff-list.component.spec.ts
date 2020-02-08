import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { TabsStaffListComponent } from './tabs-staff-list.component';
import { InitTest } from '../test/init-test';
import { StaffListComponent } from './staff-list/staff-list.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('TabsStaffListComponent', () => {
	let component: TabsStaffListComponent;
	let fixture: ComponentFixture<TabsStaffListComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TabsStaffListComponent, StaffListComponent],
			providers: [],
			imports: [RouterTestingModule.withRoutes([])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TabsStaffListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
