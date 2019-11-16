import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditBadgesComponent } from './project-audit-badges.component';

describe('ProjectAuditBadgesComponent', () => {
	let component: ProjectAuditBadgesComponent;
	let fixture: ComponentFixture<ProjectAuditBadgesComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditBadgesComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectAuditBadgesComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
