import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { doesNotReject } from 'assert';
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
			declarations: [ SummaryComponent, TestHostComponent ]
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
		expect(component).toBeTruthy();
		fixture.detectChanges();
		const service = TestBed.inject(SummaryService);
		service.showOverallAverage();
		fixture.detectChanges();
		service.summary$.subscribe({
			next: sum => {
				console.log ('nope', sum);
				expect(fixture.debugElement.query(By.css('#logo'))).toBeNull();
				expect(fixture.debugElement.query(By.css('#summaries'))).toBeDefined();
				expect(fixture.debugElement.query(By.css('#small-logo'))).toBeDefined();
				done();
			}
		})
	});

});
