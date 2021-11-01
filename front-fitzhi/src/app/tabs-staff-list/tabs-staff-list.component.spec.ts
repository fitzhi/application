import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { ListCriteria } from '../data/listCriteria';
import { StaffListContext } from '../data/staff-list-context';
import { InitTest } from '../test/init-test';
import { TabsStaffListService } from './service/tabs-staff-list.service';
import { StaffListComponent } from './staff-list/staff-list.component';
import { TabsStaffListComponent } from './tabs-staff-list.component';

describe('TabsStaffListComponent', () => {
	let component: TabsStaffListComponent;
	let fixture: ComponentFixture<TabsStaffListComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TabsStaffListComponent, StaffListComponent],
			providers: [],
			imports: [RouterTestingModule.withRoutes([])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		fixture = TestBed.createComponent(TabsStaffListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	}));

	it('Only the help container should be visible when any tab is created', done => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeDefined();
		done();
	});

	it('The creation of a tab hides the help container', done => {
		component.tabKeys.push('title');
		component.tabs.push('title');
		const tabsStaffListService = TestBed.inject(TabsStaffListService);
		tabsStaffListService.staffListContexts.set('title', new StaffListContext(new ListCriteria('criteria', false)));
		fixture.detectChanges();
		console.log (fixture.debugElement.query(By.css('#help-search')));
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeNull();
		done();
	});
});
