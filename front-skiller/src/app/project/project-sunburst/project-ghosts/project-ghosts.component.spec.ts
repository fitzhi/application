import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectGhostsComponent } from './project-ghosts.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('ProjectGhostsComponent', () => {
	let component: ProjectGhostsComponent;
	let fixture: ComponentFixture<ProjectGhostsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectGhostsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
