import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditTaskComponent } from './audit-task.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('AuditTaskComponent', () => {
	let component: AuditTaskComponent;
	let fixture: ComponentFixture<AuditTaskComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditTaskComponent ],
			imports: []
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditTaskComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
