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
	});

	it('should correctly cleanup the Staff object before the data transfert.', () => {
		expect(service).toBeTruthy();
		const staff = require('./staff-22.json');
		const cleanStaff = service.cleanupStaff(staff);
		expect(cleanStaff.missions).toBeNull();
		expect(cleanStaff.experiences).toBeNull();
	});

});
