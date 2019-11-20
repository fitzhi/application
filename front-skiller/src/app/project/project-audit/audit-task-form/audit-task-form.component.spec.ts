import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditTaskFormComponent } from './audit-task-form.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('AuditTaskComponent', () => {
	let component: AuditTaskFormComponent;
	let fixture: ComponentFixture<AuditTaskFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditTaskFormComponent ],
			imports: []
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditTaskFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
