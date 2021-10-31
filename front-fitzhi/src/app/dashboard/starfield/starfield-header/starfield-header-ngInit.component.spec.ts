import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { StarfieldService } from '../service/starfield.service';
import { StarfieldHeaderComponent } from './starfield-header.component';


describe('StarfieldHeaderComponent', () => {
	let component: StarfieldHeaderComponent;
	let fixture: ComponentFixture<StarfieldHeaderComponent>;
	let starfieldService: StarfieldService;
	let spyNext: jasmine.Spy;
	let spyPrevious: jasmine.Spy;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ StarfieldHeaderComponent ],
			providers: [StarfieldService, StaffService, FileService, MessageBoxService],
			imports: [MatDialogModule, HttpClientTestingModule, MatCheckboxModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StarfieldHeaderComponent);
		component = fixture.componentInstance;
		starfieldService = TestBed.inject(StarfieldService);
		spyPrevious = spyOn(starfieldService, 'retrieveActiveStatePrevious').and.returnValue(null);
		spyNext = spyOn(starfieldService, 'retrieveActiveStateNext').and.returnValue(null);
		fixture.detectChanges();
	});

	it('should retrieve the previous & next constellations during ngInt().', () => {
		expect(spyPrevious).toHaveBeenCalled();
		expect(spyNext).toHaveBeenCalled();
	});

});
