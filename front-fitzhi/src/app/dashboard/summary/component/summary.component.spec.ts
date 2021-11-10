import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { doesNotReject } from 'assert';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SummaryService } from '../service/summary.service';

import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'test-host-component',
		template: `	<div style="width: 800px; height: 800px; top: 0; bottom: 0; left: 0; right: 0; position: fixed; background-color: transparent;">
						<app-summary></app-summary>
					</div>`
	})

	class TestHostComponent {
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ SummaryComponent, TestHostComponent ],
			providers: [ SummaryService, DashboardService, ReferentialService, CinematicService ],
			imports: [ HttpClientTestingModule, MatDialogModule ]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should display the main logo at startup.', () => {
		expect(component).toBeTruthy();
		expect(fixture.debugElement.query(By.css('#logo'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#summaries'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#small-logo'))).toBeNull();
	});

	it('show the small logo when the first summary is loaded.', done => {
		fixture.detectChanges();
		const service = TestBed.inject(SummaryService);
		service.showGeneralAverage();
		fixture.detectChanges();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#logo'))).toBeNull();
				expect(fixture.debugElement.query(By.css('#summaries'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#small-logo'))).toBeDefined();
				done();
			}
		})
	});

	it('should display the general average panel.', done => {
		fixture.detectChanges();
		const service = TestBed.inject(SummaryService);
		service.showGeneralAverage();
		fixture.detectChanges();
		service.summary$.subscribe({
			next: sum => {
				expect(fixture.debugElement.query(By.css('#general-average'))).toBeDefined();
				done();
			}
		})
	});

});
