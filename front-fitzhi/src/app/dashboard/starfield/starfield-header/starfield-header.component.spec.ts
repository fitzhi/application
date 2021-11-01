import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { ControlledRisingSkylineModule } from 'controlled-rising-skyline';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Star } from '../data/star';
import { StarfieldService } from '../service/starfield.service';
import { StarfieldHeaderComponent } from './starfield-header.component';


describe('StarfieldHeaderComponent', () => {
	let component: StarfieldHeaderComponent;
	let fixture: ComponentFixture<StarfieldHeaderComponent>;
	let starfieldService: StarfieldService;

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
		fixture.detectChanges();
	});

	it('should be correctly created.', () => {
		expect(component).toBeTruthy();
	});

	it('should take in account every new version of the array of stars.', done => {
		expect(document.getElementById('numberOfStars').innerText).toBe('0');
		const stars = [];
		stars.push(new Star(1, 1));
		starfieldService.broadcastStars(stars);
		fixture.detectChanges();
		setTimeout(() => {
			expect(document.getElementById('numberOfStars').innerText).toBe('1');
			done();
		}, 0);

	});

	it('should broadcast a new version of the constellations if the user checks, or unchecks, the external checkbox.', fakeAsync(() => {
		const spy1 = spyOn(starfieldService, 'switchExternalFilter').and.callThrough();
		const spy2 = spyOn(starfieldService, 'generateAndBroadcastConstellations');
		const cbExternal = fixture.debugElement.query(By.css('#external'));
		cbExternal.triggerEventHandler('change', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		expect(spy1).toHaveBeenCalled();
		expect(spy2).toHaveBeenCalled();
	}));

	it('should have the buttons "next" and "previous" disabled by default.', () => {
		expect(document.getElementById('btPrevious').classList.value).toBe('px-2 button-direction-disable');
		expect(document.getElementById('btNext').classList.value).toBe('px-2 button-direction-disable');
	});

	it('should activate the buttons "next" and "previous".', done => {
		expect(document.getElementById('btPrevious').classList.value).toBe('px-2 button-direction-disable');
		expect(document.getElementById('btNext').classList.value).toBe('px-2 button-direction-disable');
		starfieldService.switchActiveStateNext(true);
		fixture.detectChanges();
		expect(document.getElementById('btNext').classList.value).toBe('px-2 button-direction');
		starfieldService.switchActiveStatePrevious(true);
		fixture.detectChanges();
		expect(document.getElementById('btPrevious').classList.value).toBe('px-2 button-direction');
		done();
	});

	it('should handle a click on the NEXT button. The selected month should be displayed on the header.', fakeAsync(() => {
		component.nextMonthAvailable = true;

		// This selected month should be displayed on header.
		starfieldService.selectedMonth.year = 2020;
		starfieldService.selectedMonth.month = 10;

		const spy = spyOn(starfieldService, 'broadcastNextConstellations').and.returnValue(null);
		const btNext = fixture.debugElement.query(By.css('#btNext'));
		btNext.triggerEventHandler('click', null);
		tick();
		fixture.detectChanges();
		expect(spy).toHaveBeenCalled();
		const selectedMonth = fixture.debugElement.query(By.css('#selectedMonth'));
		// We add 1 to display the month because the month in IS is inside the range of 0/11.
		expect(selectedMonth.nativeElement.innerText).toBe('11/2020');
	}));

	it('should handle a click on the PREVIOUS button. The selected month should be displayed on the header.', fakeAsync(() => {
		component.previousMonthAvailable = true;

		// This selected month should be displayed on header.
		starfieldService.selectedMonth.year = 2020;
		starfieldService.selectedMonth.month = 10;

		const spy = spyOn(starfieldService, 'broadcastPreviousConstellations').and.returnValue(null);
		const btNext = fixture.debugElement.query(By.css('#btPrevious'));
		btNext.triggerEventHandler('click', null);
		tick();
		fixture.detectChanges();
		expect(spy).toHaveBeenCalled();
		const selectedMonth = fixture.debugElement.query(By.css('#selectedMonth'));
		// We add 1 to display the month because the month in IS is inside the range of 0/11.
		expect(selectedMonth.nativeElement.innerText).toBe('11/2020');
	}));

	it('should display by default the current date on the header.', fakeAsync(() => {
		const today = new Date();
		const month = today.getMonth() + 1;
		const year = today.getFullYear();
		const selectedMonth = fixture.debugElement.query(By.css('#selectedMonth'));
		// We add 1 to display the month because the month in IS is inside the range of 0/11.
		expect(selectedMonth.nativeElement.innerText).toBe(`${month}/${year}`);
	}));

});
