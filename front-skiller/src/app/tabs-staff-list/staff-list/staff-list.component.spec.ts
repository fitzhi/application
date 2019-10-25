import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffListComponent } from './staff-list.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('StaffListComponent', () => {
	let component: StaffListComponent;
	let fixture: ComponentFixture<StaffListComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
