import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TreemapHeaderComponent } from './treemap-skills-header.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { Component } from '@angular/core';

describe('TreemapHeaderComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="width:100%; height: 100%; background-color: red">
				<app-treemap-skills-header></app-treemap-skills-header>
			</div>`})
		class TestHostComponent {
			constructor() {
			}
		}
				
	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapHeaderComponent, TagifyStarsComponent, TestHostComponent ],
			imports: [MatCheckboxModule]
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
