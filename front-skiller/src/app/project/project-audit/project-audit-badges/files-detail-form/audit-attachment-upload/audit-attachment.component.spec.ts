import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditAttachmentComponent } from './audit-attachment.component';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';

describe('AuditAttachmentComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: 	'<app-audit-attachment ' +
						'[idTopic]="1" >' +
					'</app-audit-attachment>'
	})
	class TestHostComponent {
	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditAttachmentComponent, TestHostComponent ],
			imports: [FormsModule, MatDialogModule, HttpClientTestingModule, MatDialogModule],
			providers: [
				ReferentialService
			]
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
