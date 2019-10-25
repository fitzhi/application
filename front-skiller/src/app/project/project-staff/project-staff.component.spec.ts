import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectStaffComponent } from './project-staff.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('ProjectStaffComponent', () => {
	let component: ProjectStaffComponent;
	let fixture: ComponentFixture<ProjectStaffComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectStaffComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
