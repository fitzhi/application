import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageBoxComponent } from './message-box.component';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { HttpClientModule } from '@angular/common/http';

describe('MessageBoxComponent', () => {
	let component: MessageBoxComponent;
	let fixture: ComponentFixture<MessageBoxComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			providers: [{
				provide: MatDialogRef,
				useValue: {}
			}, {
				provide: MAT_DIALOG_DATA,
				useValue: {} // Add any data you wish to test if it is passed/used correctly
			}],
			declarations: [MessageBoxComponent],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([])]
			})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(MessageBoxComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create!', () => {
		expect(component).toBeTruthy();
	});
});
