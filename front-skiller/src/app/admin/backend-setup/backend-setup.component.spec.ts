import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackendSetupComponent } from './backend-setup.component';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { DebugElement } from '@angular/core';
import { of } from 'rxjs';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';

describe('BackendSetupComponent', () => {
	let component: BackendSetupComponent;
	let debugElement: DebugElement;
	let fixture: ComponentFixture<BackendSetupComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(BackendSetupComponent);
		component = fixture.componentInstance;
		debugElement = fixture.debugElement;
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
		expect('http://localhost:8080/api').toEqual(field('#url').value);
	});

	it('Setup the backend URL server saved in the localStorage', () => {
		localStorage.setItem('backendUrl', 'saved://url:8000');
		fixture.detectChanges();
		expect('saved://url:8000').toEqual(field('#url').value);
	});

	it('Submit a VALID url for the very first connection', () => {
		localStorage.removeItem('backendUrl');
		fixture.detectChanges();
		expect('http://localhost:8080/api').toEqual(field('#url').value);

		const backendSetupService = TestBed.get(BackendSetupService);
		const spy = spyOn(backendSetupService, 'isVeryFirstConnection').and.returnValue(of('true'));

		expect(field('#submitButton').getAttribute('class')).toEqual('urlEdition');

		component.onSubmit();

		// We succeed to connect to the given URL. The localStorage should be updated.
		const savedUrl  = localStorage.getItem('backendUrl');
		expect(savedUrl).toEqual('http://localhost:8080/api');

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
		expect('http://localhost:8080/api').toEqual(field('#url').value);

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
