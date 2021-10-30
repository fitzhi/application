import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixtureAutoDetect, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { disconnect } from 'process';
import { take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { FileService } from 'src/app/service/file.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Constellation } from '../data/constellation';
import { StarfieldService } from './starfield.service';


describe('StarfieldService', () => {
	let service: StarfieldService;
	let staffListService: StaffListService;
	let httpTestingController: HttpTestingController;

	function allStaff() {

		const staff1 = new Collaborator();
		staff1.idStaff = 1;
		staff1.active = true;
		staff1.external = false;
		staff1.experiences = [];
		staff1.experiences.push(new Experience(1, 'One', 3));
		staff1.experiences.push(new Experience(2, 'Two', 1));
		staff1.experiences.push(new Experience(3, 'Three', 5));

		const staff2 = new Collaborator();
		staff2.idStaff = 2;
		staff2.active = true;
		staff2.external = false;
		staff2.experiences = [];
		staff2.experiences.push(new Experience(1, 'One', 1));
		staff2.experiences.push(new Experience(3, 'Three', 2));

		const staff3 = new Collaborator();
		staff3.idStaff = 3;
		staff3.active = false;
		staff3.external = false;
		staff3.experiences = [];
		staff3.experiences.push(new Experience(1, 'One', 4));

		const theStaff = [];
		theStaff.push(staff1);
		theStaff.push(staff2);
		theStaff.push(staff3);

		return theStaff;
	}

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [StarfieldService, StaffService, FileService, MessageBoxService, MessageService, BackendSetupService],
			imports: [MatDialogModule, HttpClientTestingModule]
		});
		service = TestBed.inject(StarfieldService);
		staffListService = TestBed.inject(StaffListService);
		httpTestingController = TestBed.inject(HttpTestingController);
		const backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('TEST_URL');
	});

	it('should be successfully created.', () => {
		expect(service).toBeTruthy();
	});

	it('should correctly assemble the constellations.', done => {
		const constellations = [];
		constellations.push(new Constellation(1, 2, 'black', 'lightGreen'));
		constellations.push(new Constellation(2, 3));
		service.assembleTheStars(constellations);

		service.stars$.subscribe({
			next: stars => {
				expect(stars.length).toBe(5);
				expect(stars[0].idSkill).toBe(1);
				expect(stars[1].idSkill).toBe(1);
				expect(stars[2].idSkill).toBe(2);
				expect(stars[3].idSkill).toBe(2);
				expect(stars[4].idSkill).toBe(2);
				done();
			}
		});
	});

	it('should manage correctly an empty staff collection.', () => {
		const constellations = service.takeStaffInAccount([]);
		expect(constellations.length).toBe(0);
	});

	it('should generate the constellations based on the staff collection.', () => {
		const constellations = service.takeStaffInAccount(allStaff());
		expect(constellations.length).toBe(3);
		expect(constellations.find(c => c.idSkill === 1).count).toBe(4);
		expect(constellations.find(c => c.idSkill === 2).count).toBe(1);
		expect(constellations.find(c => c.idSkill === 3).count).toBe(7);
	});

	it('should GENERATE & BROADCAST the constellations based on the staff collection.', done => {

		const spy1 = spyOn(service, 'generateAndBroadcastConstellations').and.callThrough();
		const spy2 = spyOn(service, 'broadcastConstellations').and.callThrough();

		staffListService.allStaff$.next(allStaff());
		service.generateAndBroadcastConstellations();
		service.constellations$.subscribe({
			next: constellations => {
				expect(spy1).toHaveBeenCalled();
				expect(spy2).toHaveBeenCalled();
				expect(constellations.length).toBe(3);
				done();
			}
		});
	});

	it('should exclude from the generation of constellations the external staff members.', () => {
		const staff = allStaff();
		staff.find(st => st.idStaff === 2).external = true;

		// We exclude the external developers
		service.filter.external = false;

		let constellations = service.takeStaffInAccount(staff);
		expect(constellations.length).toBe(3);
		expect(constellations.find(c => c.idSkill === 1).count).toBe(3);
		expect(constellations.find(c => c.idSkill === 2).count).toBe(1);
		expect(constellations.find(c => c.idSkill === 3).count).toBe(5);

		// We include the external developers in the computing.
		service.filter.external = true;

		constellations = service.takeStaffInAccount(staff);
		expect(constellations.length).toBe(3);
		expect(constellations.find(c => c.idSkill === 1).count).toBe(4);
		expect(constellations.find(c => c.idSkill === 2).count).toBe(1);
		expect(constellations.find(c => c.idSkill === 3).count).toBe(7);

	});

	it('should be able to retrieve the constellations of the PREVIOUS month and therefore activate the PREVIOUS button.', done => {
		service.retrieveActiveStatePrevious();
		const req = httpTestingController.expectOne('TEST_URL/api/staff/constellation/2021/9');
		expect(req.request.method).toBe('GET');
		req.flush([]);
		service.previous$.subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBe(true);
				done();
			}
		});
	});

	it('should handle the lack of constellations for the PREVIOUS month.', done => {
		service.retrieveActiveStatePrevious();
		const req = httpTestingController.expectOne('TEST_URL/api/staff/constellation/2021/9');
		expect(req.request.method).toBe('GET');
		const error = new ErrorEvent('error');
		req.error(
			error,
			{
				status: 404,
				statusText: 'Not found!',
			});

		service.previous$.subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBe(false);
				done();
			}
		});
	});

	it('should evaluate correctly the next month for the 30/10/2021.', () => {
		const next = service.nextMonth(new Date(2021, 10, 30));
		expect(next.getFullYear()).toBe(2021);
		expect(next.getMonth()).toBe(11);
	});

	
	it('should evaluate correctly the next month for the 30/11/2021 (Be aware that month are evaluated from 0 to 11).', () => {
		const next = service.nextMonth(new Date(2021, 11, 30));
		expect(next.getFullYear()).toBe(2022);
		expect(next.getMonth()).toBe(0);
	});


	it('should be able to retrieve the constellations of the NEXT month, and therefore activate the NEXT button.', done => {
		service.retrieveActiveStateNext();
		const req = httpTestingController.expectOne('TEST_URL/api/staff/constellation/2021/11');
		expect(req.request.method).toBe('GET');
		req.flush([]);
		service.next$.subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBe(true);
				done();
			}
		});
	});

	it('should handle the lack of constellations for the NEXT month.', done => {
		service.retrieveActiveStateNext();
		const req = httpTestingController.expectOne('TEST_URL/api/staff/constellation/2021/11');
		expect(req.request.method).toBe('GET');
		const error = new ErrorEvent('error');
		req.error(
			error,
			{
				status: 404,
				statusText: 'Not found!',
			});

		service.next$.subscribe({
			next: doneAndOk => {
				expect(doneAndOk).toBe(false);
				done();
			}
		});
	});

});
