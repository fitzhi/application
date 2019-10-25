import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TabsStaffListComponent } from './tabs-staff-list.component';
import { RootTestModule } from '../root-test/root-test.module';
import { StaffListComponent } from './staff-list/staff-list.component';

describe('TabsStaffListComponent', () => {
	let component: TabsStaffListComponent;
	let fixture: ComponentFixture<TabsStaffListComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
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
