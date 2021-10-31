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


describe('StarfieldService.broadcastCollections()', () => {
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

	it('should broadcast the NEXT constellations from the server.', done => {
		service.selectedMonth.year = 2020;
		service.selectedMonth.month = 8;

		// We return FALSE to force the load from server.
		const spyOne = spyOn(service, 'nextMonthIsCurrentMonth').and.returnValue(false);

		const spyTwo = spyOn(service, 'generateConstellations').and.returnValue([]);
		const spyThree = spyOn(service, 'assembleTheStars').and.returnValue(null);
		const spyFour = spyOn(service, 'switchActiveStateNext').and.returnValue(null);
		const spyFive = spyOn(service, 'switchActiveStatePrevious').and.returnValue(null);
		const spySix = spyOn(service, 'retrieveActiveStateNext').and.returnValue(null);
		const spySeven = spyOn(service, 'retrieveActiveStatePrevious').and.returnValue(null);
		service.broadcastNextConstellations();
		expect(spyOne).toHaveBeenCalled();
		expect(spyTwo).toHaveBeenCalled();
		expect(spyThree).toHaveBeenCalled();
		expect(spyFour).toHaveBeenCalled();
		expect(spyFive).toHaveBeenCalled();
		expect(spySix).toHaveBeenCalled();
		expect(spySeven).toHaveBeenCalled();

		expect(service.selectedMonth.year).toBe(2020);
		expect(service.selectedMonth.month).toBe(9);

		done();
	});

	it('should broadcast the NEXT constellations from the staff evaluation.', done => {
		service.selectedMonth.year = new Date().getFullYear();
		service.selectedMonth.month = new Date().getMonth();

		// We return FALSE to force the load from server.
		const spyOne = spyOn(service, 'nextMonthIsCurrentMonth').and.returnValue(true);
		const spyTwo = spyOn(service, 'generateAndBroadcastConstellations').and.returnValue(null);

		const spyFour = spyOn(service, 'switchActiveStateNext').and.returnValue(null);
		const spyFive = spyOn(service, 'switchActiveStatePrevious').and.returnValue(null);
		const spySix = spyOn(service, 'retrieveActiveStateNext').and.returnValue(null);
		const spySeven = spyOn(service, 'retrieveActiveStatePrevious').and.returnValue(null);
		service.broadcastNextConstellations();
		expect(spyOne).toHaveBeenCalled();
		expect(spyTwo).toHaveBeenCalled();

		expect(spyFour).toHaveBeenCalled();
		expect(spyFive).toHaveBeenCalled();
		expect(spySix).toHaveBeenCalled();
		expect(spySeven).toHaveBeenCalled();

		const next = service.nextMonth(new Date());
		expect(service.selectedMonth.year).toBe(next.getFullYear());
		expect(service.selectedMonth.month).toBe(next.getMonth());

		done();
	});

	it('should broadcast the PREVIOUS constellations from the server.', done => {
		service.selectedMonth.year = 2020;
		service.selectedMonth.month = 8;

		const spyTwo = spyOn(service, 'generateConstellations').and.returnValue([]);
		const spyThree = spyOn(service, 'assembleTheStars').and.returnValue(null);
		const spyFour = spyOn(service, 'switchActiveStateNext').and.returnValue(null);
		const spyFive = spyOn(service, 'switchActiveStatePrevious').and.returnValue(null);
		const spySix = spyOn(service, 'retrieveActiveStateNext').and.returnValue(null);
		const spySeven = spyOn(service, 'retrieveActiveStatePrevious').and.returnValue(null);
		service.broadcastPreviousConstellations();
		expect(spyTwo).toHaveBeenCalled();
		expect(spyThree).toHaveBeenCalled();
		expect(spyFour).toHaveBeenCalled();
		expect(spyFive).toHaveBeenCalled();
		expect(spySix).toHaveBeenCalled();
		expect(spySeven).toHaveBeenCalled();

		expect(service.selectedMonth.year).toBe(2020);
		expect(service.selectedMonth.month).toBe(7);

		done();
	});

});
