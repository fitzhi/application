import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { AuditUploadAttachmentComponent } from './audit-upload-attachment.component';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { HttpClientModule } from '@angular/common/http';
import { MatSliderModule } from '@angular/material/slider';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { InitTest } from 'src/app/test/init-test';

describe('AuditUploadAttachmentComponent', () => {
	let component: AuditUploadAttachmentComponent;
	let fixture: ComponentFixture<AuditUploadAttachmentComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [AuditUploadAttachmentComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {}},
				{ provide: MAT_DIALOG_DATA, useValue: {} },
			],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(AuditUploadAttachmentComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
