import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AuditAttachmentComponent } from './audit-attachment.component';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';

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

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ AuditAttachmentComponent, TestHostComponent ],
			imports: [FormsModule, MatDialogModule, HttpClientTestingModule, MatDialogModule],
			providers: [
				ReferentialService, CinematicService
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
