import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { FirstConnection } from 'src/app/data/first-connection';
import { BackendSetupService } from '../../service/backend-setup/backend-setup.service';
import { CinematicService } from '../../service/cinematic.service';
import { ReferentialService } from '../../service/referential/referential.service';
import { BackendSetupComponent } from './backend-setup.component';


describe('BackendSetupComponent', () => {
	let component: BackendSetupComponent;
	let debugElement: DebugElement;
	let fixture: ComponentFixture<BackendSetupComponent>;

	beforeEach(waitForAsync(() => {
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

		const backendSetupService = TestBed.inject(BackendSetupService);
		const spy = spyOn(backendSetupService, 'isVeryFirstConnection$').and.returnValue(of(new FirstConnection(true, null)));

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

		const backendSetupService = TestBed.inject(BackendSetupService);
		const spy = spyOn(backendSetupService, 'isVeryFirstConnection$')
			.and.returnValue(throwError({code: 500, message: 'Error message'}));

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
