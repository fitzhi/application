import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { NOT_MODIFIED } from 'http-status-codes';
import { take } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
import { Collaborator } from '../../data/collaborator';
import { InitTest } from '../../test/init-test';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { StaffListListenerService } from './staff-list-listener.service';
import { StaffListService } from './staff-list.service';


describe('ListStaffService', () => {

	let service: StaffListService;
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let staffListListenerService: StaffListListenerService;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [BackendSetupService],
			imports: [HttpClientTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();

		service = TestBed.inject(StaffListService);

		httpTestingController = TestBed.inject(HttpTestingController);

		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		staffListListenerService = TestBed.inject(StaffListListenerService);

	}));

	function createStaff(): Collaborator {
		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.firstName = 'mock Frédéric';
		staff.lastName = 'mock VIDAL';
		return staff;
	}

	it('should load the staff list from the backend.', done => {

		const spyTakeInAccountStaff = spyOn(service, 'takeInAccountStaff').and.callThrough();

		expect(service).toBeTruthy();
		expect(service.allStaff.length).toBe(0);
		service.reloadStaff();

		const staff = createStaff();

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/staff');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush( [ staff ] );

		service.allStaff$.pipe(take(1)).subscribe({
			next: () => {
				expect(service.allStaff.length).toBe(1);
				expect(service.allStaff[0].idStaff).toBe(1789);
				done();
			}
		});

		expect(spyTakeInAccountStaff).toHaveBeenCalled();

	});

	it('should clean the staff list when loading the list from the backend.', done => {

		const spyTakeInAccountStaff = spyOn(service, 'takeInAccountStaff').and.callThrough();

		const staff = createStaff();

		expect(service).toBeTruthy();
		service.allStaff = [staff]
		expect(service.allStaff.length).toBe(1);

		service.reloadStaff();

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/staff');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush( [ staff ] );

		service.allStaff$.pipe(take(1)).subscribe({
			next: () => {
				expect(service.allStaff.length).toBe(1);
				expect(service.allStaff[0].idStaff).toBe(1789);
				done();
			}
		});

		expect(spyTakeInAccountStaff).toHaveBeenCalled();

	});

	it('should handle an error when loading the staff list from the backend.', done => {

		const spyTakeInAccountStaff = spyOn(service, 'takeInAccountStaff').and.callThrough();
		const spyInterruptListener = spyOn(staffListListenerService, 'interruptStaffListener');

		expect(service).toBeTruthy();
		expect(service.allStaff.length).toBe(0);
		service.reloadStaff();

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/staff');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush(
			{},
			{
				headers: {},
				status: 400,
				statusText: 'Bad request'
			}
		);

		service.allStaff$.pipe(take(2)).subscribe({
			next: staff => {
				expect(service.allStaff.length).toBe(0);
				done();
			}
		});

		expect(spyTakeInAccountStaff).not.toHaveBeenCalled();
		expect(spyInterruptListener).toHaveBeenCalled();
	});

	it('should keep the transferred ETag in the session storage after the staff loading is complete.', done => {

		const spyTakeInAccountStaff = spyOn(service, 'takeInAccountStaff').and.callThrough();
		const spySaveEtag = spyOn(service, 'saveEtag').and.callThrough();
		const spyInterruptListener = spyOn(staffListListenerService, 'interruptStaffListener');

		expect(service).toBeTruthy();
		const staff =  createStaff();

		service.reloadStaff();

		const reqApi1 = httpTestingController.expectOne('URL_OF_SERVER/api/staff');
		expect(reqApi1.request.method).toEqual('GET');
		reqApi1.flush(
			[ staff ],
			{
				headers: { 'ETag': 'a_testing_Etag' }
			}
		);

		service.allStaff$.pipe(take(2)).subscribe({
			next: () => {
				expect(service.allStaff.length).toBe(1);
				done();
			}
		});

		expect(spyTakeInAccountStaff).toHaveBeenCalled();
		expect(sessionStorage.getItem(Constants.ETAG_STAFF)).toBe('a_testing_Etag');
		expect(spySaveEtag).toHaveBeenCalled();
		expect(spyInterruptListener).not.toHaveBeenCalled();
	});

	it('should NOT interrupt the Staff listener loop if the server returned an NOT_MODIFIED status error (ETag feature).', done => {

		spyOn(service, 'takeInAccountStaff').and.returnValue(null);
		const spyStaffListenerService = spyOn(staffListListenerService, 'interruptStaffListener').and.returnValue(null);

		service.reloadStaff();

		const reqGetProjects = httpTestingController.expectOne('URL_OF_SERVER/api/staff');
		expect(reqGetProjects.request.method).toEqual('GET');
		reqGetProjects.error(new ErrorEvent('error'), { status: NOT_MODIFIED, statusText: 'Staff unchanged' });

		expect(spyStaffListenerService).not.toHaveBeenCalled();
		done();
	});

});


