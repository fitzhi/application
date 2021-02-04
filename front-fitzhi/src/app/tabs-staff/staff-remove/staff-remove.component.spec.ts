import { async, ComponentFixture, TestBed, ɵTestingCompiler } from '@angular/core/testing';

import { StaffRemoveComponent } from './staff-remove.component';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { Collaborator } from 'src/app/data/collaborator';

describe('StaffRemoveComponent', () => {
	let component: StaffRemoveComponent;
	let fixture: ComponentFixture<StaffRemoveComponent>;
	let staffDataExchangeService: StaffDataExchangeService;
	let staffService: StaffService;
	let httpTestingController: HttpTestingController;
	let backendSetupService: BackendSetupService;
	let staffListService: StaffListService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ StaffRemoveComponent ],
			providers: [StaffService, ReferentialService, CinematicService, StaffDataExchangeService, StaffListService],
			imports: [ HttpClientTestingModule, MatDialogModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffRemoveComponent);
		component = fixture.componentInstance;

		staffService = TestBed.inject(StaffService);
		staffDataExchangeService = TestBed.inject(StaffDataExchangeService);
		staffDataExchangeService.collaborator = staffService.emptyStaff();
		staffDataExchangeService.collaborator.firstName = 'Frédéric';
		staffDataExchangeService.collaborator.lastName = 'VIDAL';
		staffDataExchangeService.collaborator.idStaff = 1964;

		httpTestingController = TestBed.inject(HttpTestingController);
		backendSetupService = TestBed.inject(BackendSetupService);
		backendSetupService.saveUrl('URL_OF_SERVER');

		staffListService = TestBed.inject(StaffListService);
		staffListService.allStaff = [];
		staffListService.allStaff.push(createCollaborator(1));
		staffListService.allStaff.push(staffDataExchangeService.collaborator);
		staffListService.allStaff.push(createCollaborator(2));
		fixture.detectChanges();
	});

	function createCollaborator(idStaff: number) {
		const collaborator = staffService.emptyStaff();
		collaborator.idStaff = idStaff;
		return collaborator;
	}

	it('should be created without error', () => {
		expect(component).toBeTruthy();
	});

	it('Should invoke projectService.removeStaff, when clicking on button', () => {

		const spy = spyOn(staffService, 'removeStaff$').and.callThrough();

		// 3 entries in the staff
		expect(staffListService.allStaff.length).toBe(3);
		expect(staffListService.findIndex(1964)).toBe(1);

		expect(component).toBeTruthy();
		const button = fixture.debugElement.nativeElement.querySelector('button');
		button.click();
		fixture.detectChanges();

		const reqDelete = httpTestingController.expectOne('URL_OF_SERVER/api/staff/1964');
		expect(reqDelete.request.method).toEqual('DELETE');
		reqDelete.flush(null);

		// 1 entry deleted, 2 entries are remaining in the staff
		expect(staffListService.allStaff.length).toBe(2);

		expect(staffListService.findIndex(1964)).toBe(-1);

	});

	afterEach(() => {
		httpTestingController.verify();
		backendSetupService.saveUrl(null);
	});

});
