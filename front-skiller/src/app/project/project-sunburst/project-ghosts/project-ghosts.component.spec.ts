import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectGhostsComponent } from './project-ghosts.component';

describe('ProjectGhostsComponent', () => {
	let component: ProjectGhostsComponent;
	let fixture: ComponentFixture<ProjectGhostsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectGhostsComponent ]
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
