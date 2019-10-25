import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffTabsComponent } from './staff-tabs.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('StaffTabsComponent', () => {
	let component: StaffTabsComponent;
	let fixture: ComponentFixture<StaffTabsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			imports: [RootTestModule]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffTabsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
