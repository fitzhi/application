import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectRemoveComponent } from './project-remove.component';

describe('ProjectRemoveComponent', () => {
	let component: ProjectRemoveComponent;
	let fixture: ComponentFixture<ProjectRemoveComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectRemoveComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectRemoveComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
