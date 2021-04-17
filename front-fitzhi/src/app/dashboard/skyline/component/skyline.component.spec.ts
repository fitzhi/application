import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SkylineComponent } from './skyline.component';

describe('SkylineComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 	`
					<div>
						<app-skyline width="370" height="1200"></app-skyline>
					</div>
					`
	})
	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ SkylineComponent ],
			providers: [DatePipe],
			imports: [HttpClientTestingModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
