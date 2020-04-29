import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { TabsStaffListComponent } from './tabs-staff-list.component';
import { InitTest } from '../test/init-test';
import { StaffListComponent } from './staff-list/staff-list.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ListCriteria } from '../data/listCriteria';
import { StaffListContext } from '../data/staff-list-context';
import { TabsStaffListService } from './service/tabs-staff-list.service';
import { By } from '@angular/platform-browser';
import { ExpectedConditions } from 'protractor';

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

	it('Only the help container should be visible when any tab is created', () => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeDefined();
	});

	it('The creation of a tab hides the help container', () => {
		component.tabKeys.push('title');
		component.tabs.push('title');
		const tabsStaffListService = TestBed.get(TabsStaffListService);
		tabsStaffListService.staffListContexts.set('title', new StaffListContext(new ListCriteria('criteria', false)));
		fixture.detectChanges();
		console.log (fixture.debugElement.query(By.css('#help-search')));
		expect(fixture.debugElement.query(By.css('#help-search'))).toBeNull();
	});
});
