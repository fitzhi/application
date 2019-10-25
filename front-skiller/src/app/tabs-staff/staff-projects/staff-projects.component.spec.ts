import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffProjectsComponent } from './staff-projects.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('StaffProjectsComponent', () => {
	let component: StaffProjectsComponent;
	let fixture: ComponentFixture<StaffProjectsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
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
