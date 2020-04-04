import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackendSetupComponent } from './backend-setup.component';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { DebugElement } from '@angular/core';
import { of } from 'rxjs';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import { MatGridListModule } from '@angular/material/grid-list';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatDialogModule } from '@angular/material/dialog';

describe('BackendSetupComponent', () => {
	let component: BackendSetupComponent;
	let debugElement: DebugElement;
	let fixture: ComponentFixture<BackendSetupComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [BackendSetupComponent],
			providers: [ReferentialService, CinematicService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(BackendSetupComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement;
	});

	afterEach(() => {
		localStorage.setItem('backendUrl', 'http://localhost:8080');
		fixture.destroy();
	});

	it('id created without error', () => {
		expect(component).toBeTruthy();
	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	it('Setup the default backend URL server', () => {
		localStorage.removeItem('backendUrl');
		fixture.detectChanges();
		expect('http://localhost:8080').toEqual(field('#url').value);
	});

	it('Setup the backend URL server saved in the localStorage', () => {
		localStorage.setItem('backendUrl', 'saved://url:8080');
		fixture.detectChanges();
		expect('saved://url:8080/api').toEqual(field('#url').value);
	});

	it('Submit a VALID url for the very first connection', () => {
		localStorage.removeItem('backendUrl');
		fixture.detectChanges();
		expect('http://localhost:8080').toEqual(field('#url').value);

		const backendSetupService = TestBed.get(BackendSetupService);
		const spy = spyOn(backendSetupService, 'isVeryFirstConnection').and.returnValue(of('true'));

		expect(field('#submitButton').getAttribute('class')).toEqual('urlEdition');

		component.onSubmit();

		// We succeed to connect to the given URL. The localStorage should be updated.
		const savedUrl  = localStorage.getItem('backendUrl');
		expect(savedUrl).toEqual('http://localhost:8080');

		expect('urlValid').toEqual(component.classButton());
		fixture.detectChanges();
		expect(field('#submitButton').getAttribute('class')).toEqual('urlValid');
		component.messengerVeryFirstConnection.subscribe(data =>
			// We are informed that this is the very first connection for this backend
			// The user to be created will be an admin user
			expect(data).toBeTruthy()
		);
	});

	it('Submit an INVALID url for the very first connection', () => {
		localStorage.removeItem('backendUrl');
		fixture.detectChanges();
		expect('http://localhost:8080').toEqual(field('#url').value);

		const backendSetupService = TestBed.get(BackendSetupService);
		const spy = spyOn(backendSetupService, 'isVeryFirstConnection')
			.and.returnValue(Observable.throw({code: 500, message: 'Error message'}));

		expect(field('#submitButton').getAttribute('class')).toEqual('urlEdition');
		component.onSubmit();

		// The submit failed. So we do not save this wrong url.
		const savedUrl  = localStorage.getItem('backendUrl');
		expect(savedUrl).toBeNull();

		expect('urlInvalid').toEqual(component.classButton());
		fixture.detectChanges();
		expect(field('#submitButton').getAttribute('class')).toEqual('urlInvalid');
	});
});
