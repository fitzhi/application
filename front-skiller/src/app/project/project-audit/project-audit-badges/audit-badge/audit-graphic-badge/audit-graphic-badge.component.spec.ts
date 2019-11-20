import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { Component } from '@angular/core';

describe('AuditGraphicBadgeComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: '<app-audit-graphic-badge [index]="1" [evaluation]="50"></app-audit-graphic-badge>'
	})

	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent, AuditGraphicBadgeComponent],
			imports: [RootTestModule]
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
