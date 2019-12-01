import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportDetailFormComponent } from './report-detail-form.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('ReportDetailFormComponent', () => {
	let component: ReportDetailFormComponent;
	let fixture: ComponentFixture<ReportDetailFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ReportDetailFormComponent ],
			imports: [MatFormFieldModule, FormsModule, ReactiveFormsModule, MatInputModule, BrowserAnimationsModule]
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
