import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditBadgesComponent } from './project-audit-badges.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('ProjectAuditBadgesComponent', () => {
	let component: ProjectAuditBadgesComponent;
	let fixture: ComponentFixture<ProjectAuditBadgesComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
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
