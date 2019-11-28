import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectAuditBadgesComponent } from './project-audit-badges.component';
import { Component } from '@angular/core';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { AuditBadgeComponent } from './audit-badge/audit-badge.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { AuditGraphicBadgeComponent } from './audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { toBase64String } from '@angular/compiler/src/output/source_map';
import { ReportDetailFormComponent } from '../report-detail-form/report-detail-form.component';

describe('ProjectAuditBadgesComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let topics = [];

	@Component({
		selector: 'app-host-component',
		template: '<app-project-audit-badges [auditTopics$]="auditTopics$"></app-project-audit-badges>'
	})
	class TestHostComponent {
		public auditTopics$ = new BehaviorSubject<any>([]);
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ProjectAuditBadgesComponent, TestHostComponent, AuditBadgeComponent,
				ReportDetailFormComponent, AuditGraphicBadgeComponent],
			imports: [RootTestModule, MatGridListModule]
		})
		.compileComponents();
	}));


	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		topics = [];
		topics.push({ id: 1, title: 'test title One'});
		topics.push({ id: 2, title: 'second test title'});
		component.auditTopics$.next(topics);
		fixture.detectChanges();
	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	it('should be created with 2 thumbnails', () => {
		expect(component).toBeTruthy();
		// 2 children expected.
		expect(field('#containerAuditThumbnails').children.length).toBe(2);
		// 1 app-audit-badge per child div
		expect(field('#containerAuditThumbnails').children[0].children.length).toBe(1);
		//
		expect (field('#topic-note-0')).toBeDefined();
		expect(field('#topic-note-0').innerHTML).toBe('50');
		expect(field('#topic-title-1').innerHTML).toBe('test title One');

	});

	it('should remove the first thumbnail if the corresponding entry in topics is removed ', () => {
		topics.splice(0, 1);
		expect(topics.length).toBe(1);

		component.auditTopics$.next(topics);
		fixture.detectChanges();
		// 1 child expected.
		expect(field('#containerAuditThumbnails').children.length).toBe(1);
		//
		expect (field('#topic-note-0')).toBeDefined();
		expect(field('#topic-note-0').innerHTML).toBe('50');
		expect(field('#topic-title-2').innerHTML).toBe('second test title');

	});

});
