import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditGraphicBadgeComponent } from './audit-graphic-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RiskLegend } from 'src/app/data/riskLegend';
import { ReferentialService } from 'src/app/service/referential.service';

describe('AuditGraphicBadgeComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: '<app-audit-graphic-badge [id]=1 [evaluation]=50 [editable]=true></app-audit-graphic-badge>'
	})

	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent, AuditGraphicBadgeComponent],
			imports: [RootTestModule, FormsModule]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		const referentialService: ReferentialService = TestBed.get(ReferentialService);
		const risk = new RiskLegend();
		risk.level = 5;
		risk.color = 'blue';
		referentialService.legends.push (risk);
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

});
