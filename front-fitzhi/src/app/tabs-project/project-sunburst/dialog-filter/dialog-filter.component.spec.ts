import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';

import { DialogFilterComponent } from './dialog-filter.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule, MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InitTest } from 'src/app/test/init-test';

describe('DialogFilterComponent', () => {

	let component: DialogFilterComponent;
	let fixture: ComponentFixture<DialogFilterComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [DialogFilterComponent],
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
		fixture = TestBed.createComponent(DialogFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create!', () => {
		expect(component).toBeTruthy();
	});
});
