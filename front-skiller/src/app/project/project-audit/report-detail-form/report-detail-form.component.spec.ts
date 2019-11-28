import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RootTestModule } from 'src/app/root-test/root-test.module';
import { ReportDetailFormComponent } from './report-detail-form.component';

describe('AuditTaskComponent', () => {
	let component: ReportDetailFormComponent;
	let fixture: ComponentFixture<ReportDetailFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ReportDetailFormComponent ],
			imports: []
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ReportDetailFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
