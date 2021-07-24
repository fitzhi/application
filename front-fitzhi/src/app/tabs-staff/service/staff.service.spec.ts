import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { StaffService } from './staff.service';

describe('staffService', () => {
	let service: StaffService;
	let httpMock: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [StaffService, FileService, MessageBoxService, CinematicService, BackendSetupService],
			imports: [HttpClientTestingModule, MatDialogModule]
		});
		service = TestBed.inject(StaffService);
		httpMock = TestBed.inject(HttpTestingController);
		const backendSetupService = TestBed.inject(BackendSetupService);
		localStorage.setItem('backendUrl', 'http://myServerUrl:8080');
	});

	it('should be simply created without error', () => {
		expect(service).toBeTruthy();
	});

	it('should CREATE a new staff if staff.idStaff IS NULL', done => {

		const staff = new Collaborator();
		staff.idStaff = -1;
		staff.firstName = 'Frederic';
		staff.lastName = 'VIDAL';

		const savedStaff = new Collaborator();
		savedStaff.idStaff = 1789;
		savedStaff.firstName = 'Frederic';
		savedStaff.lastName = 'VIDAL';

		// We mock the Staff creation
		const spyCreateStaff = spyOn(service, 'create$')
			.and.callThrough()
			.and.returnValue(of(savedStaff));

		// We mock the Staff update
		const spyUpdateStaff = spyOn(service, 'update$');

		service.save$(staff).pipe(take(1)).subscribe({
			next: st => {
				expect(st.idStaff).toBe(1789);
				expect(spyUpdateStaff).not.toHaveBeenCalled();
				done();
			}
		});
	});

	it('should UPDATE a staff member if staff.idStaff IS NOT NULL', done => {

		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.firstName = 'Frédéric';
		staff.lastName = 'VIDAL';

		const savedStaff = new Collaborator();
		savedStaff.idStaff = 1789;
		savedStaff.firstName = 'Frederic';
		savedStaff.lastName = 'VIDAL';

		// We mock the Staff creation.
		const spyCreateStaff = spyOn(service, 'create$');

		// We mock the Staff update.
		const spyUpdateStaff = spyOn(service, 'update$')
			.and.callThrough()
			.and.returnValue(of(savedStaff));

		service.save$(staff).pipe(take(1)).subscribe({
			next: st => {
				expect(st.idStaff).toBe(1789);
				expect(st.firstName).toBe('Frederic');
				expect(spyCreateStaff).not.toHaveBeenCalled();
				done();
			}
		});
	});

	it('registerUser$() for the first connection', done => {
		service.registerUser$(true, 'myUser', 'myPass').subscribe({
			next: createdStaff => {
				expect(createdStaff.idStaff).toBe(1789);
				done();
			}
		});
		const req = httpMock.expectOne('http://myServerUrl:8080/api/admin/veryFirstUser?login=myUser&password=myPass');
		expect(req.request.method).toBe('POST');
		expect(req.request.params.get('login')).toBe('myUser');
		expect(req.request.params.get('password')).toBe('myPass');
		const staff = new Collaborator();
		staff.idStaff = 1789;
		req.flush(staff);
	});

	it('registerUser$() during lifetime of the application', done => {
		service.registerUser$(false, 'myUser', 'myPass').subscribe({
			next: createdStaff => {
				expect(createdStaff.idStaff).toBe(1789);
				done();
			}
		});
		const req = httpMock.expectOne('http://myServerUrl:8080/api/admin/register?login=myUser&password=myPass');
		expect(req.request.method).toBe('POST');
		expect(req.request.params.get('login')).toBe('myUser');
		expect(req.request.params.get('password')).toBe('myPass');
		const staff = new Collaborator();
		staff.idStaff = 1789;
		req.flush(staff);
	});

	afterEach(() => {
		httpMock.verify();
	});

});
